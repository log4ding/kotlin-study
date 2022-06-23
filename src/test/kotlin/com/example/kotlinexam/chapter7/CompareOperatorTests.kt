package com.example.kotlinexam.chapter7

import org.junit.jupiter.api.Test
import java.lang.IndexOutOfBoundsException
import java.time.LocalDate

class CompareOperatorTests {
    @Test
    fun test() {
        println(Point2(10, 20) == Point2(10, 20))
        println(Point2(10, 20) != Point2(5, 5))
        println(null == Point2(1, 2))
    }

    @Test
    fun compareToTest() {
        val p1 = Person("Alice", "Smith")
        val p2 = Person("Bob", "Johnson")
        println(p1 < p2)
    }

    @Test
    fun test3() {
        val p1 = Point2(10, 20)
        println(p1[1])

        val p2 = MutablePoint(10, 20)
        p2[1] = 42
        println(p2)
    }

    @Test
    fun test4() {
        val rect = Rectangle(Point2(10, 20), Point2(50, 50))
        println(Point2(20, 30) in rect)
        println(Point2(5, 5) in rect)
    }

    @Test
    fun rangeToTest() {
        // rangeTo 관례
        // 범위를 만들려면 .. 구문을 사용해야 한다. rangeTo는 ..를 풀어 표현하는 방법
        val now = LocalDate.now()
        val vacation = now..now.plusDays(10)
        println(now.plusWeeks(1) in vacation)

        val n = 9
        println(0..(n+1))
    }

    @Test
    fun localDateTests() {
        val newYear = LocalDate.ofYearDay(2017, 1)
        val daysOff = newYear.minusDays(1)..newYear
        for (dayOff in daysOff) {
            println(dayOff)
        }
    }
}

// 동등 연산자
class Point2(val x: Int, val y : Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true
        if (obj !is Point2) return false
        return obj.x == x && obj.y == y
    }
}

// compareTo
class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person) : Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

// get 관례 구하기
operator fun Point2.get(index: Int): Int {
    return when(index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

// set 관례
data class MutablePoint(var x: Int, var y: Int)
operator fun MutablePoint.set(index: Int, value: Int) {
    when(index) {
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

// in 관례
data class Rectangle(val upperLeft: Point2, val lowerRight: Point2)
operator fun Rectangle.contains(p: Point2): Boolean {
    return p.x in upperLeft.x until lowerRight.x &&
            p.y in upperLeft.y until lowerRight.y
}
// 열린 범위 는 끝 값을 포함하지 않는 범위, 10..20 의 범위일 경우 닫힌 범위는 10 이상 20 이하, 10.until 20 으로 만들면 10~19

// for 루프를 위한 iterator 관례
// operator fun CharSequence.iterator(): CharIterator
// 위 operator는 for (c in "abc")를 가능하게 해준다
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object: Iterator<LocalDate> {
        var current = start
        override fun hasNext() = current <= endInclusive
        override fun next() = current.apply {
            current = plusDays(1)
        }
    }