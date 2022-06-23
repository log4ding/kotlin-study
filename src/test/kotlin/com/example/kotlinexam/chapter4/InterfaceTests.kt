package com.example.kotlinexam.chapter4

import org.junit.jupiter.api.Test

class InterfaceTests {
    @Test
    fun test() {
        Button().showOff()

        val gahee = TestUser("gahee6567@naver.com")
        println(gahee.nickname)

        val user = User4("Alice")
        user.address = "서울시 광진구"
    }
}

interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!") // default function
}

class Button: Clickable, Focusable {
    override fun click() = println("I was clicked")
//    override fun showOff() { // 이름이 같은 함수가 있을 경우 명시적으로 새로운 구현을 제공 해야함
//        super<Clickable>.showOff() // <>로 어떤 클래스의 함수를 호출할지 정의할 수 있다.
//        super<Focusable>.showOff()
//    }
    override fun showOff() = super<Focusable>.showOff() // 단일 지정도 가능함
}

interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")

    fun showOff() = println("I'm focusable!")
}


// 인터페이스의 프로퍼티 구현하기
interface User2 {
    val nickname: String
}

class PrivateUser(override val nickname: String): User2
class SubscribeUser(val email: String) : User2 {
    override val nickname: String
        get() = email.substringBefore("@")
}
class FacebookUser(val accountId: Int): User2 {
    override val nickname: String
        get() = accountId.toString()
}

// email도 프로퍼티로 넣는다면?
interface User3 {
    val email: String
    val nickname: String
        get() = email.substringBefore("@")
}
// nickname은 override할 필요가 없지만, email은 override 해야함
class TestUser(override val email: String) : User3

// getter, setter backing field
class User4(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("""
                Address was changed for $name:
                "$field" -> "$value".
            """.trimIndent())
            field = value
        }
}

// setter에 접근 제어자 변경
class LengthCounter {
    var counter: Int = 0
        private set
    fun addWord(word: String) {
        counter += word.length
    }
}