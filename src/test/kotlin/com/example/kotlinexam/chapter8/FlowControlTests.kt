package com.example.kotlinexam.chapter8

import org.junit.jupiter.api.Test

class flowControlTests {
    @Test
    fun test() {
        val people = listOf(Person1("Alice", 29), Person1("Bob", 31))
        lookForAlice(people)
    }

    @Test
    fun test1() {
        val people = listOf(Person1("Alice", 29), Person1("Bob", 31))
        lookForAlice2(people)

        lookForAlice4(people)
    }
}

// 람다 안의 return 문: 람다를 둘러싼 함수로부터 반환
fun lookForAlice(people: List<Person1>) {
    for (person in people) {
        if (person.name == "Alice") {
            println("Found!")
            return
        }
        println("Alice is not found")
    }

    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return
        // 람다 안에서 return을 사용할 경우 람다로부터만 반환되는 게 아니라 호출하는 함수가 실행을 끝내고 반환된다.
        // -> 이렇게 자신을 둘러싼 블록보다 바깥에 있는 블록을 반환하게 하는 것을 넌로컬(non local) return 이라고 부른다.
        // 넌로컬 return이 될 때는 람다를 인자로 받는 함수가 인라인 함수인 경우
        // forEach는 인라인 함수이므로 람다 본문과 함께 인라이닝 된다.
        // 인라이닝 되지 않는 함수에서는 람다 안에서 return을 사용할 수 없다.
        }
    }
    println("Alice is not found")
}

// 람다로부터 반환: 레이블을 사용한 return
// label을 사용한다면 로컬 반환을 사용할 수 있다.
fun lookForAlice2(people: List<Person1>) {
    people.forEach label@{
        if (it.name == "Alice") return@label
    }

    println("Alice might be somewhere") // 항상 이 줄이 출력됨
}

// 함수 이름을 return 레이블로 사용하기
fun lookForAlice3(people: List<Person1>) {
    people.forEach {
        if (it.name == "Alice") return@forEach // 람다 식으로부터 반환시킴
    }

    println("Alice might be somewhere")

    // label 사용 예
    println(StringBuilder().apply sb@ {
        listOf(1, 2, 3).apply {
            this@sb.append(this.toString()) // 바깥쪽 묵시적 수신 객체에 접근할 때는 레이블을 명시해야 한다.
        }
    })
}

// 무명함수 : 기본적으로 로컬 return
fun lookForAlice4(people: List<Person1>) {
    people.forEach(fun (person) {
        if (person.name == "Alice") return
        println("${person.name} is not Alice")
    })

    // 무명 함수는 상위 블록의 함수를 return 시키지 못한다
    // filter 함수에 무명함수 쓰기
    people.filter(fun (person) : Boolean {
        return person.age < 30
    })

    people.filter(fun (person) = person.age < 30)
}