package com.example.kotlinexam.ddd

import org.junit.jupiter.api.Test
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*
import kotlin.test.BeforeTest

private val JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;"

class Chapter3 {
    private fun createTable() {
        DriverManager.getConnection(JDBC_URL, "sa", "")
            .use { connection ->
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS user (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255))")
                    .use { preparedStatement ->
                        preparedStatement.executeUpdate()
                    }
            }
    }

    @BeforeTest
    fun beforeTest() {
        createTable()
    }

    @Test
    fun test() {
        val testUserRepository = TestUserRepository()
        val userService = UserService(testUserRepository)
        val program = UserApplicationService(userService, testUserRepository)
        val user = program.register("john")
        val user2 = program.register("jane")
    }

    @Test
    fun appTest() {
        val userRepository = TestUserRepository()
        val userService = UserService(userRepository)
        val testApplicationService = UserApplicationService(userService, userRepository)
        // 사용자 명만 변경
        testApplicationService.update(
            UserUpdateCommand(
                id = "1234",
                name = "john",
            )
        )

        // 이메일만 변경
        testApplicationService.update(
            UserUpdateCommand(
                id = "1234",
                email = "xxxx@example.com",
            )
        )
    }
}

data class User(
    val id: UserId = UserId(UUID.randomUUID().toString()),
    var name: UserName?,
    var email: EmailAddress? = null,
) {
    fun changeName(newName: UserName) {
        name = newName
    }

    fun changeEmail(newEmail: EmailAddress) {
        email = newEmail
    }
}

@JvmInline
value class EmailAddress(val value: String) {
    init {
        require(value.length > 3) { "이메일은 3글자 이상이어야 합니다. $value" }
    }
}

@JvmInline
value class UserName(val value: String) {
    init {
        require(value.isNotBlank()) { "사용자 명은 꼭 등록해야합니다. $value" }
        require(value.length > 3) { "사용자 명은 3글자 이상이어야 합니다. $value" }
    }
}

data class UserData private constructor(
    val id: String,
    val name: String,
) {
    constructor(source: User) : this(source.id.value, source.name?.value ?: "")
}

class Client(
    private val userRegisterService: IUserRegisterService
) {
    fun register(name: String) {
        userRegisterService.handle(UserRegisterCommand(name))
    }
}

interface IUserRegisterService {
    fun handle(command: UserRegisterCommand)
}

class MockUserRegisterService : IUserRegisterService {
    override fun handle(command: UserRegisterCommand) {
        println("MockUserRegisterService")
    }
}

class MockExceptionUserRegisterService : IUserRegisterService {
    override fun handle(command: UserRegisterCommand) {
        throw java.lang.IllegalArgumentException()
    }
}

data class UserUpdateCommand(
    val id: String,
    val name: String? = null,
    val email: String? = null,
)

data class UserDeleteCommand(
    val id: String,
)

class UserApplicationService(
    private val userService: UserService,
    private val userRepository: IUserRepository,
) {
    fun register(name: String): User {
        val userName = UserName(name)
        val user = User(name = userName)
        val isExists = userService.isExists(user)
        if (isExists) {
            throw IllegalArgumentException("이미 존재하는 사용자입니다.")
        }

        userRepository.save(user)

        return user
    }

    fun get(userId: String): UserData? {
        val targetId = UserId(userId)
        val user = userRepository.findById(targetId.value)
        return user?.let { UserData(it) }
    }

    fun update(command: UserUpdateCommand) {
        val targetId = UserId(command.id)
        val user =
            userRepository.findById(targetId.value) ?: throw IllegalArgumentException("$targetId 는 존재하지 않는 사용자입니다.")
        command.name?.let {
            user.changeName(UserName(command.name))
            if (userService.isExists(user)) {
                throw IllegalArgumentException("이미 존재하는 사용자 명입니다.")
            }
        }
        command.email?.let {
            user.changeEmail(EmailAddress(command.email))
        }

        userRepository.save(user)
    }
}

class UserService(
    private val userRepository: IUserRepository,
) {
    fun isExists(user: User): Boolean {
//        val users = user.name?.let {
//            userRepository.find(it.value)
//        }
        val users = user.email?.let {
            userRepository.findByEmail(it.value)
        }
        return users.isNullOrEmpty().not()
    }
}

data class UserRegisterCommand(
    val name: String,
)

class UserRegisterService(
    private val userService: UserService,
    private val userRepository: TestUserRepository,
) {
    fun handle(command: UserRegisterCommand) {
        val user = User(name = UserName(command.name))
        if (userService.isExists(user)) {
            throw IllegalArgumentException("이미 존재하는 사용자입니다.")
        }

        userRepository.save(user)
    }
}

class UserDeleteService(
    private val userRepository: TestUserRepository,
) {
    fun handle(command: UserDeleteCommand) {
        val targetId = UserId(command.id)
        val user =
            userRepository.findById(targetId.value) ?: return
        userRepository.delete(user.id.value)
    }
}
interface IUserRepository {
    fun save(user: User)
    fun find(userName: String): List<User>
    fun findByEmail(email: String): List<User>
    fun findById(userId: String): User?
    fun delete(userId: String)
}

class TestUserRepository : IUserRepository {
    override fun save(user: User) {
        val sql = """
            MERGE INTO user
            USING (
                SELECT ? AS id, ? AS name FROM dual
            ) AS DATA
            ON user.id = DATA.id
            WHEN MATCHED THEN
                UPDATE SET name = DATA.name
            WHEN NOT MATCHED THEN
                INSERT (id, name) VALUES (DATA.id, DATA.name)
        """.trimIndent()
        DriverManager.getConnection(JDBC_URL, "sa", "")
            .use { connection ->
                connection.prepareStatement(sql)
                    .use { preparedStatement ->
                        preparedStatement.setString(1, user.id.value)
                        preparedStatement.setString(2, user.name?.value)
                        preparedStatement.executeUpdate()
                    }
            }
    }

    override fun find(userName: String): List<User> {
        return DriverManager.getConnection(JDBC_URL, "sa", "")
            .use { connection ->
                connection.prepareStatement("SELECT * FROM user WHERE name = ?")
                    .use { preparedStatement ->
                        preparedStatement.setString(1, userName)
                        val rs: ResultSet = preparedStatement.executeQuery()

                        val results = mutableListOf<User>()
                        while (rs.next()) {
                            results.add(
                                User(
                                    id = UserId(rs.getString("id")),
                                    name = UserName(rs.getString("name")),
                                )
                            )
                        }
                        return results
                    }
            }
    }

    override fun findByEmail(email: String): List<User> {
        TODO("Not yet implemented")
    }

    override fun findById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override fun delete(userId: String) {
        TODO("Not yet implemented")
    }

}

//class PhysicalDistributionBase {
//    fun ship(baggage: Baggage): Baggage {
//
//    }
//
//    fun receive(baggage: Baggage): Unit {
//
//    }
//
//    fun transport(to: PhysicalDistributionBase, baggage: Baggage): Unit {
//        val shippedBaggage = ship(baggage)
//        to.receive(shippedBaggage)
//
//        // 운송 기록 같은 걸 기록한다.
//    }
//}
//
//class TransportService {
//    fun transport(to: PhysicalDistributionBase, baggage: Baggage): Unit {
//        val shippedBaggage = ship(baggage)
//        to.receive(shippedBaggage)
//
//        // 운송 기록 같은 걸 기록한다.
//    }
//}

class UserTestApplicationService {
    private var sendMail: Boolean = false

    fun register(name: String) {
        if (sendMail) {
            // 메일을 보낸다.
        }
    }
}