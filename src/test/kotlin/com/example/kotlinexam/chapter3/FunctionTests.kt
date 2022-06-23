package com.example.kotlinexam.chapter3

import org.junit.jupiter.api.Test
import com.example.kotlinexam.chapter3.lastChar as last // 다른 이름으로 alias

// 최상위 클래스에 global 선언
var opCount = 0
fun performOperation() {
    opCount ++
}
fun reportOperationCount() {
    println("Operation performed $opCount times")
}

class FunctionTests {
    @Test
    fun test() {
        val set = hashSetOf(1, 7, 53)
        val list = arrayListOf(1, 7, 53)
        val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

        println(set.javaClass)
        println(list.javaClass)
        println(map.javaClass)
    }

    @Test
    fun test2() {
        val strings = listOf("first", "second", "fourteenth")
        println(strings.last())

        val numbers = setOf(1, 14, 2)
        println(numbers.maxOrNull())
    }

    @Test
    fun test3() {
        val list = listOf(1, 2, 3)
        println(list.joinToString("; ", "(", ")"))
        println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))

        val list2 = listOf("1", "2", "3")
        println(list2.join(" "))
    }

    @Test
    fun test4() {
        println("Kotlin".last)

        val sb = StringBuilder("Kotlin?")
        sb.last = '!'
        println(sb)
    }
}