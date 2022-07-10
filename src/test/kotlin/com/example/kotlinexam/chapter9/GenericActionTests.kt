package com.example.kotlinexam.chapter9

import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import java.util.*

class GenericActionTests {
    @Test
    fun test() {
        printSum(listOf(1, 2, 3))
        printSum(setOf(1, 2, 3)) // 예외 발생
        printSum(listOf("1", "2", "3")) // as? 캐스팅은 성공하지만 다른 예외 발생
    }

    @Test
    fun test2() {
        println(isA<String>("abc"))

        println(isA<String>(123))

        // filterIsInstance
        val items = listOf("one", 2, "three")
        println(items.filterIsInstance<String>()) // 이 함수에서는 실행 시점에 타입 인자를 알 수 있어서 해당 타입에 해당하는 원소만 추출할 수 있다.
    }
}

// JVM의 제네릭스는 type erasure(타입 소거)를 사용해 구현됨
// 함수를 inline으로 선언하면 타입 인자가 지워지지 않게 할 수 있다. (= relify)
// 코틀린의 제네릭 타입 인자 정보는 런타임에 지워진다.
// 실행 시점에 어떤 리스트가 어떤 타입으로 이루어진 건지 검사할 수 없다.
// if (value is List<String>) 같은 코드를 쓸 수 없다. -> 에러 발생
// 실행 시점에서는 타입 정보의 크기가 줄어들어 메모리 사용량이 줄어든다는 제네릭 타입 소거 나름의 장점이 있음
// 그럼 실행 시점에 List인지 맵인지 구분하려면 ? star projection을 사용하면 된다.
// if (value is List<*>)
// 인자를 알 수 없는 제네릭 타입을 표현할 때 사용, 자바에서는 List<?>와 비슷

// 제네릭 타입으로 타입 캐스팅 하기
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> ?: throw IllegalAccessException("List is expected")
    println(intList.sum())
}

// 알려진 타입 인자를 사용해 타입 검사하기
fun printSum2(c: Collection<Int>) {
    if (c is List<Int>) {
        println(c.sum())
    }
}

// 안전하지 못한 is 검사는 금지하고, 위험한 as 캐스팅은 경고를 출력함

// 제네릭 클래스의 인스턴스가 있어도 그 인스턴스를 만들 때 사용한 타입 인자를 알아낼 수가 없다.
//fun <T> isA(value: Any) = value is T => 에러 발생

// 실체화한 타입 파라미터를 사용하는 함수 정의하기
inline fun <reified T> isA(value: Any) = value is T

// filterIsInstance를 간단하게 정리한 버전
inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> {
    val destination = mutableListOf<T>()
    for (element in this) {
        if (element is T) {
            destination.add(element)
        }
    }

    return destination
}

// inline 함수에서는 타입 인자를 쓸 수 있는 이유
// 본문을 구현한 바이트 코드를 그 함수가 호출되는 모든 지점에 삽입하는데 컴파일러는 실체화한 타입 인자를 사용해 인라인 함수를 호출하는 각 부분의 정확한 타입 인자를 알 수 있다.

// 표준 자바 API 인 ServiceLoader를 사용해 서비스를 읽어들이려면 다음 코드처럼 호출해야 한다
val serviceImpl = ServiceLoader.load(Service::class.java)
// 구체화한 타입 파라미터를 사용하면 아래와 같다
val serviceImpl2 = loadService<Service>()

// loadService 함수를 구현해보자.
inline fun <reified T> loadService() : ServiceLoader<T> {
    return ServiceLoader.load(T::class.java)
}
