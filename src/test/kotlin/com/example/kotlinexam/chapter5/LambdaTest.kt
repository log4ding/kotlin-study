package com.example.kotlinexam.chapter5

import com.example.kotlinexam.chapter3.joinToString
import org.junit.jupiter.api.Test
import java.lang.StringBuilder

class LambdaTest {
    @Test
    fun test() {
        // lambda
        val people = listOf(Person("Alice", 29), Person("Bob", 31))
        println(people.maxByOrNull { it.age })
        println(people.maxByOrNull(Person::age))

        people.maxByOrNull { p -> p.age }
    }

    @Test
    fun test2() {
        // lambda 식 직접 호출
        {println(42)}()
        // run을 이용한 호출
        run { println(42) }
    }

    @Test
    fun test3() {
        val people = listOf(Person("이몽룡", 29), Person("성춘향", 31))
        val names = people.joinToString(separator = " ", transform = {p:Person -> p.name})
        println(names)
    }

    @Test
    fun test4() {
        printMessageWithPrefix(listOf("1", "2", "3"), "string")

        printProblemCounts(listOf("200 ok", "418 I'm a teapot", "500 Internal Server Error"))

        val getAge = Person::age
    }

    @Test
    fun test5() {
        val natualNumbers = generateSequence(0) { it + 1 }
        val numbersTo100 = natualNumbers.takeWhile { it <= 100 }
        println(numbersTo100.sum())
    }
}

data class Person(val name: String, val age: Int)

fun findTheOldest(people: List<Person>) {
    var maxAge = 0
    var theOldest: Person? = null
    for (person in people) {
        if (maxAge < person.age) {
            maxAge = person.age
            theOldest = person
        }
    }
}

fun printMessageWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach {
        println("${prefix} ${it}")
    }
}

fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0
    responses.forEach {
        if (it.startsWith("4")) {
            clientErrors++
        } else if (it.startsWith("5")) {
            serverErrors++
        }
    }

    println("$clientErrors client errors, $serverErrors server errors")
}


// apply 를 통해 만들기
fun alphabet() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }

    append("\nNow I know the alphabet!")
}

// with 를 통해 만들기
fun alphabet2() = with(StringBuilder()) {
    for (letter in 'A'..'Z') {
        this.append(letter)
    }
    append("\nNow I know the alphabet!")
}