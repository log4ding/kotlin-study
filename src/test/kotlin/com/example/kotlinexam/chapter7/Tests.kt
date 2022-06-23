package com.example.kotlinexam.chapter7

import org.junit.jupiter.api.Test
import java.math.BigDecimal

class Tests {
    @Test
    fun test() {
        val p1 = Point(10, 20)
        val p2 = Point(30, 40)
        println(p1 + p2)
    }

    @Test
    fun test2() {
        val p = Point(10, 20)
        println(p * 1.5)

        println('a' * 3)
    }

    @Test
    fun bitOperator() {
        println(0x0F and 0xF0)
        println(0x0F or 0xF0)
        println(0x1 shl 4)
    }

    @Test
    fun test3() {
        val numbers = ArrayList<Int>()
        numbers += 42
        println(numbers[0])
    }

    @Test
    fun test4() {
        var bd = BigDecimal.ZERO
        println(bd++)
        println(++bd)
    }
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

operator fun Point.times(scale: Double): Point {
    return Point((x*scale).toInt(), (y*scale).toInt())
}

operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}

// += operator는 plusAssign 으로 사용한다.

// 단항 연산자 오버로딩
operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}

// 증가 연산자 정의
operator fun BigDecimal.inc() = this + BigDecimal.ONE
