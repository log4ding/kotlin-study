package com.example.kotlinexam.chapter6

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.StringReader
import java.lang.NumberFormatException

class CollectionsTests {
    @Test
    fun test() {
        val reader = BufferedReader(StringReader("1\nabc\n42"))
        val numbers = readNumbers(reader)
        addValidNumbers2(numbers)
    }

    @Test
    fun test2() {
        val source: Collection<Int> = arrayListOf(3, 5, 7)
        val target: MutableCollection<Int> = arrayListOf(1) // MutableCollection으로 선언했기 때문에 변경이 가능
        copyElements(source, target)
        println(target)
    }

    @Test
    fun test3() {
        val letters = Array<String>(26) { i -> ('a' + i).toString() }
        println(letters.joinToString(""))

        val strings = listOf("a", "b", "c")
        println("%s/%s/%s".format(*strings.toTypedArray())) // vararg 인자를 넘기기 위해 스프레드 연산자(*)를 써야 한다.
    }
}

fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()
    for (line in reader.lineSequence()) {
        try {
            val number = line.toInt()
            result.add(number)
        } catch (e: NumberFormatException) {
            result.add(null)
        }
    }

    return result
}

fun addValidNumbers(numbers: List<Int?>) {
    var sumOfValidNumbers = 0
    var invalidNumbers = 0
    for (number in numbers) {
        if (number != null) {
            sumOfValidNumbers += number
        } else {
            invalidNumbers ++
        }
    }

    println("Sum Of valid Numbers: $sumOfValidNumbers")
    println("Invalid Numbers: $invalidNumbers")
}

// filterNotNull 널이 될 수 있는 값으로 이뤄진 컬렉션에 대해 사용하기
fun addValidNumbers2(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid Numbers: ${validNumbers.sum()}")
    println("Invalid numbers : ${numbers.size - validNumbers.size}")
}

// 원본 데이터 변경을 막기 위해 복사하는 패턴을 defensive copy 라고 한다.
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

// 코틀린에서 배열 사용하기
fun test(args: Array<String>) {
    for (i in args.indices) {
        println("Argument $i is: ${args[i]}")
    }
}

// arrayOf로 배열을 생성할 수 있다.
// arrayOfNulls 함수로 모든 원소가 null이고 인자로 넘긴 값과 크기가 같은 배열을 만들 수 있다.
//