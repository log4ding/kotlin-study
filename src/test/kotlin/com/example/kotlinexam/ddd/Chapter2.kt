package com.example.kotlinexam.ddd

import com.example.kotlinexam.chapter6.isNullOrBlank
import org.junit.jupiter.api.Test

class DomainDrivenStudy2Test {
    @Test
    fun test() {
        val testUser = TestUser(UserId("1"), "gahee")
        println(testUser)
    }

    @Test
    fun useTest() {
        val userId = UserId("id")
        val userName = "name"
        val testUser = TestUser(userId, userName)

        val checkId = UserId("check")
        val checkName = "check"
        val checkTestUser = TestUser(checkId, checkName)

        val duplicateCheckResult = checkTestUser.isExists(testUser)
    }

    @Test
    fun serviceTest() {
        val userId = UserId("id")
        val userName = "name"
        val testUser = TestUser(userId, userName)

        val testUserService = TestUserService()
        val duplicateCheckResult = testUserService.isExists(testUser)

    }

    fun check(leftTestUser: TestUser, rightTestUser: TestUser) {
        if (leftTestUser == rightTestUser) {
            println("같은 사용자입니다.")
        } else {
            println("다른 사용자입니다.")
        }
    }
}

data class TestUser(
    val id: UserId,
    var name: String
) {
    init {
        validate(name)
    }

    private fun validate(name: String) {
        require(!name.isNullOrBlank()) { "사용자 명은 꼭 등록해야합니다. $name" }
        require(name.length > 3) { "사용자 명은 3글자 이상이어야 합니다. $name"}
    }

    fun isExists(testUser: TestUser): Boolean {
        // 사용자명 중복을 확인하는 코드
        return false
    }

    fun changeName(newName: String) {
        validate(name)
        this.name = newName
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return this.id == (other as TestUser).id
    }
}

@JvmInline
value class UserId(val value: String) {
    init {
        require(!value.isNullOrBlank()) { "id must not be blank" }
    }
}

class TestUserService {
    fun isExists(testUser: TestUser): Boolean {
        // 사용자명 중복을 확인하는 코드
        return false
    }

    fun changeName(testUser: TestUser, newName: String) {
        testUser.changeName(newName)
    }
}