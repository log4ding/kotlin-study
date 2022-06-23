package com.example.kotlinexam.chapter4

import org.junit.jupiter.api.Test
import javax.naming.Context
import javax.swing.text.AttributeSet

class InitialTests {
    @Test
    fun test() {
        val gahee = User("dingu")
        println(gahee.isSubscribed)

        val hye = User("혜원", isSubscribed = false)
        println(hye.isSubscribed)
    }
}

open class User(val nickname: String,
    val isSubscribed: Boolean = true) // 생성자에도 default 파라미터 작성 가능

// 위 선언된 코드를 풀어보자.
//class User constructor(_nickname: String) { // 주 생성자
//    val nickname: String
//    init {  // 초기화 블록
//        nickname = _nickname
//    }
//}


// 기반 클래스를 상속받으면 ()가 무조건 들어간다, 그래서 인터페이스 상속과 클래스 상속을 구분할 수 있다.
class TwitterUser(nickname: String): User(nickname)

open class View2 {
    constructor(ctx: Context) {}
    constructor(ctx: Context, attr: AttributeSet) {}
}

class MyButton: View2 {
    constructor(ctx: Context): super(ctx)
//    constructor(ctx: Context) : this(ctx, MY_STYLE) // attr 파라미터가 있는 생성자로 위임
    constructor(ctx: Context, attr: AttributeSet): super(ctx, attr)
}