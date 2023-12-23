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
        val program = Program(userService, testUserRepository)
        val user = program.createUser("john")
        val user2 = program.createUser("jane")
    }
}

data class User(
    val id: UserId = UserId(UUID.randomUUID().toString()),
    var name: UserName?,
)

@JvmInline
value class UserName(val value: String) {
    init {
        require(value.isNotBlank()) { "사용자 명은 꼭 등록해야합니다. $value" }
        require(value.length > 3) { "사용자 명은 3글자 이상이어야 합니다. $value" }
    }
}

class Program(
    private val userService: UserService,
    private val testUserRepository: TestUserRepository,
) {
    fun createUser(name: String): User {
        val userName = UserName(name)
        val user = User(name = userName)
        val isExists = userService.isExists(user)
        if (isExists) {
            throw IllegalArgumentException("이미 존재하는 사용자입니다.")
        }

        testUserRepository.save(user)

        return user
    }
}

class UserService(
    private val testUserRepository: TestUserRepository,
) {
    fun isExists(user: User): Boolean {
        val users = user.name?.let {
            testUserRepository.find(it.value)
        }
        return users.isNullOrEmpty().not()
    }

    fun changeName(testUser: TestUser, newName: String) {
        testUser.changeName(newName)
    }
}

interface IUserRepository {
    fun save(user: User)
    fun find(userName: String?): List<User>
}

class TestUserRepository : IUserRepository {
    override fun save(user: User) {
//        val sql = """
//            insert into user(id, name) values (?, ?)
//        """.trimIndent()

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

    override fun find(userName: String?): List<User> {
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