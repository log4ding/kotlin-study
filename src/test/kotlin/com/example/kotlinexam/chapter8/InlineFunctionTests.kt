package com.example.kotlinexam.chapter8

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileReader
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

// inline 변경자를 어떤 함수에 붙일 경우 컴파일러는 그 함수를 호출하는 모든 문장을 함수 본문에 해당하는 바이트 코드로 바꿔치기 해준다
// inline을 선언하면 함수 본문이 인라인 된다. 함수 본문을 번역한 바이트 코드로 컴파일 한다는 뜻
// 람다는 보통 무명클래스로 컴파일 되기 때문에 무명클래스를 생성하는 비용이 들 수 밖에 없다.
// 이 비용을 줄이기 위해 활용되는 방법이 inline

class InlineFunctionTests {
    @Test
    fun test() {
        // 만약 아래와 같은 함수가 있다 치면
        fun foo(l: Lock) {
            println("Before Sync")
            synchronized(l) {
                println("Action")
            }
            println("After Sync")
        }

        // 위 함수는 아래와 같은 바이트 코드를 만들어낸다.
        fun __foo__(l: Lock) {
            println("Before Sync")
            l.lock()
            try {
                println("Action")
            } finally {
                l.unlock()
            }
            println("After Sync")
        }

        // 아래와 같은 코드가 또 있다면
        class LockOwner(val lock: Lock) {
            fun runUnderLock(body: () -> Unit) {
                synchronized(lock, body)
            }
        }

        // 이렇게 바이트 코드로 변환된다
        class LockOwnerForByteCode(val lock: Lock) {
            fun __runUnderLock__(body: () -> Unit) {
                lock.lock()
                try {
                    body()
                } finally {
                    lock.unlock()
                }
            }
        }
    }

    @Test
    fun test1() {
        val people = listOf(Person1("Alice", 29), Person1("Bob", 31))
        // filter 함수로 거르기
        println(people.filter { it.age < 30 })

        // 직접 거르기
        val result = mutableListOf<Person1>()
        for (person in people) {
            if (person.age < 30) result.add(person)
        }
        println(result)

        // filter와 map 함수는 인라인 함수다 -> 그래서 그 두 함수의 본문은 인라이닝 되며 추가 객체나 클래스 생성은 없음
        // 처리할 원소가 많아질 경우엔 asSequence 를 통해 리스트 대신 시퀀스를 사용하면 부가 비용이 줄어듦
        // 중간 시퀀스는 람다를 필드에 저장하는 객체로 표현됨

        // 그러나, 지연 계산을 통해 성능을 향상시키려는 이유로 모든 컬렉션 연산에 asSequence를 붙여서는 안된다.
        // 시퀀스 연산에서는 람다가 인라이닝되지 않아서 크기가 작은 컬렉션은 일반 컬렉션 연산이 더 나을 수도 있다.
    }

    // 자원 관리를 위한 람다 사용
    @Test
    fun test4() {
        val l : Lock
//        l.withLock {  }
        // 자바의 try-with-resource = 코틀린의 use
        fun readFirstLineFromFile(path: String): String {
            BufferedReader(FileReader(path)).use { br ->
                return br.readLine() // 여기서 사용된 return은 넌로컬 return
            }
        }
    }
}

// inline 함수 정의
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    }
    finally {
        lock.unlock()
    }
}

// 인라인 함수의 한계
// - 람다를 사용하는 모든 함수를 인라이닝 할 수는 없다.
// - 인라인 함수의 본문에서 람다 식을 바로 호출하거나, 람다 식을 인자로 전달받아 바로 호출하는 경우에는 그 람다를 인라이닝할 수 있다.
class TransformingSequence<T, R>(val sequence : Sequence<T>, transform: (T) -> R): Sequence<R> {
    override fun iterator(): Iterator<R> {
        TODO("Not yet implemented")
    }
}
fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
    return TransformingSequence(this, transform)
}
// 위 예제에서 TransformingSequence 클래스 안에서 transform 람다를 받아 프로퍼티로 저장한다.
// 그렇게 되면 transform 인자를 인라이닝하지 않는 함수 표현으로 만들 수 밖에 없다. -> 즉 무명클래스 인스턴스로 만들어야 함

// 둘 이상의 람다를 인자로 받는 함수에서 일부 람다만 인라이닝하고 싶다면 noinline 변경자를 파라미터 이름 앞에 붙여서 금지할 수 있다.
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {}


// 컬렉션 연산 인라이닝
// 람다를 이용해 컬렉션 거르기
data class Person1(val name: String, val age: Int)

// 함수를 인라인으로 선언해야 하는 경우
// 인라이닝하는 함수의 크기가 작을 때만 인라인을 사용하는 것이 좋다. 클 경우 바이트 코드가 커지는 효과를 낳을수 있기 때문

// 자원 관리를 위해 인라인된 람다 사용
// 여기서 자원은 file, Lock, Database transaction 등 여러 다른 대상을 가리킬 수 있다.
// synchronized나 try/finally 같은 문보다 코틀린은 withLock 이라는 함수가 있다.
