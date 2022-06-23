package com.example.kotlinexam.chapter8

import org.junit.jupiter.api.Test
import java.lang.StringBuilder

class HigherOrderFunction {
    @Test
    fun test() {
        val url = "http://kotl.in"
        performRequest(url) { code, content -> println(code) }

        twoAndThree{ a, b -> a + b }
    }

    @Test
    fun test2() {
        val letters = listOf("Alpha", "Beta")
        println(letters.joinToString())

        println(letters.joinToString2{it.lowercase()})

        println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.uppercase() }))
    }

    @Test
    fun test3() {
        val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
        println("Shipping costs ${calculator(Order(3))}")
    }
}

//val sum = { x : Int, y : Int -> x + y }
//val action = { println(42) }

val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }

// 반환 타입이 널일 가능성
var canReturnNull: (Int, Int) -> Int? = { x, y -> null }
// 함수 타입 전체가 널일 가능성
var funOrNull: ((Int, Int) -> Int)? = null

// 파라미터 이름과 함수 타입
fun performRequest(
    url: String,
    callback: (code: Int, content: String) -> Unit
) {

}

// 인자로 받은 함수 호출
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

// filter
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }

    return sb.toString()
}

// 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

// 함수 타입의 파라미터에 대한 디폴트 값 지정하기
fun <T> Collection<T>.joinToString2(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() }
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }
    result.append(postfix)
    return result.toString()
}

// 널이 될 수 있는 함수 타입 파라미터를 사용하기
fun <T> Collection<T>.joinToString3(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element) ?: element.toString()
        result.append(str)
    }
    result.append(postfix)
    return result.toString()
}

// 함수에서 함수를 반환하기
enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)
fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}