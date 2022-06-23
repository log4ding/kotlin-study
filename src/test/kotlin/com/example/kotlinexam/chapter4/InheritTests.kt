package com.example.kotlinexam.chapter4

import java.io.Serializable

class InheritTests {
}

// open -> 상속 가능
// 코틀린은 기본이 final -> 상속 불가능
open class RichButton : Clickable {
    fun disable() {}
    open fun animate() {}
//    override fun click() {}
    final override fun click() {} // 상속을 하고 싶지 않다면 final로 선언
}

// 추상 클래스 사용 가능
abstract class Animated {
    abstract fun animate()
    open fun stopAnimating() {}
    fun animateTwice() {}
}

// 중첩 클래스, 내부 클래스
interface State : Serializable
interface View {
    fun getCurrentState(): State
    fun restoreState(state: State)
}

class Button2 : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) {
        TODO("Not yet implemented")
    }

    class ButtonState: State
}

// outer 클래스를 inner 클래스에서 접근하려면 this@outer 로 작성해줘야한다.
class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}

// sealed로 선언할 경우 봉인되어서 else 구문으로 Num, Sum이 아닐 타입을 체크할 필요가 없어진다.
// sealed 로 표기할 경우 open(상속 가능이 되버림 -> default final임)
sealed class Expr {
    class Num(val Value: Int): Expr()
    class Sum(val left: Expr, val right: Expr): Expr()
}

fun eval(e: Expr) : Int =
    when (e) {
        is Expr.Num -> e.Value
        is Expr.Sum -> eval(e.left) + eval(e.right)
    }