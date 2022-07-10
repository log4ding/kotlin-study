package com.example.kotlinexam.chapter9

import org.junit.jupiter.api.Test

class GenericTypeParameterTests {
    @Test
    fun test() {
        val letters = ('a'..'z').toList()
        println(letters.slice<Char>(0..2))
        println(letters.slice(10..13))  // 컴파일러가 여기서 T가 Char 인걸 추론한다.

        // 9.2 제네릭 고차 함수 호출하기
        val authors = listOf("Dmitry", "Sveltlna")
        val readers = mutableListOf<String>("Dmitry", "James", "Julia")
        readers.filter {it !in authors}
    }

    @Test
    fun test2() {
        println(max("kotlin", "java"))

        // 컴파일 에러
//        println(max("kotlin", 32))
    }
}

// List<Any> 를 인자로 받는 함수에게 List<Int> 타입의 값을 전달할 수 있을지 여부는 use-site variance (사용 지점 변성) 같은 목표를
// 제네릭 타입 값을 사용하는 위치에서 파라미터 타입에 대한 제약을 표시하는 방식으로 달성함 (ex. 자바 와일드 카드 같은 것)
//fun <T> List<T>.slice(indices: IntRange): List<T> // 제네릭 함수 slice는 T를 타입 파라미터로 받음

// 주의점
// 제네릭한 일반 프로퍼티는 말이 되지 않음
// val <T> x: T = TODO() // ERROR

// 9.1.3 타입 파라미터 제약
// Sum 같은 함수를 List<Int>나 List<Double> 엔 사용할 수 있지만 List<String> 에는 사용할 수 없음
// 어떤 제네릭 타입의 타입 파라미터에 대한 상한(upper bound)을 지정하면 그 제네릭 타입을 인스턴스화 할 때 사용하는 타입 인자는 반드시 그 상한 타입이거나 그 상한 타입의 하위 타입 이어야 한다.
// fun <T: Number> List<T>.sum(): T
// ㄴ 보통 자바에서는 T extends Number 이런식으로 표현

fun <T: Number> oneHalf(value: T): Double {
    return value.toDouble() / 2.0
}

fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}

// 타입 파라미터에 여러 제약을 가하기
fun <T> ensureTrailingPeriod(seq: T)
    where T: CharSequence, T: Appendable { // 타입 파라미터 제약 목록 정할 수 있는 where
        if (!seq.endsWith('.')) {
            seq.append('.')
        }
    }

class Processor<T> {
    fun process(value: T) {
        value?.hashCode() // value는 널이 될 수 있다. 따라서 안전한 호출을 사용해야 한다.
    }
}

// 위 Processor 에서 널이 될 수 없는 타입만 받으려면 제약을 가해야 한다.
// Any? 대신 Any를 상한으로 사용
class Processor2<T: Any> {
    fun process(value: T) {
        value.hashCode()
    }
}