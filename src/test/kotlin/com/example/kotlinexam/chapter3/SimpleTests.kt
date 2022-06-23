package com.example.kotlinexam.chapter3

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class SimpleTests {
    @Test
    fun test() {
//        println("Hello World!")
//        val question = "인생의 즐거움은 축구다.";
//        val answer: Int = 42;
//
//        val name = if (answer > 40) "" else "Kotlin"
//        println("Hello! ${name}")
//
//        println(mix(Color.BLUE, Color.YELLOW))
//        println(eval(Sum(Num(1), Num(2))))

//        for ( i in 100 downTo 1 step 2) {
//            print(fizzBuzz(i))
//        }

//        val binaryReps = TreeMap<Char, String>()
//        for (c in 'A'..'F') {
//            val binary = Integer.toBinaryString(c.toInt())
//            binaryReps[c] = binary
//        }
//
//        for ((letter, binary) in binaryReps) {
//            println("$letter = $binary")
//        }
//
//        // list 인덱스와 함께 출력
//        val list = arrayListOf("10", "11", "1001")
//        for ((index, element) in list.withIndex()) {
//            println("$index : $element")
//        }

        println(isLetter('B'))
        println(isNotDigit('8'))

    }

    fun max(a: Int, b: Int) : Int = if (a > b) a else b
}

class Person(val name: String) {
    val age: Int
        get() {
            return 13
        }
}

enum class Color(val r: Int, val g: Int, val b: Int) {
    RED(255,0,0), ORANGE(255,165,0),
    YELLOW(255,255,0), GREEN(0,255,0),
    BLUE(0,0,255), INDIGO(75,0,130),
    VIOLET(238,130,238);

    fun rgb() = (r*256+g) * 256*b
}

fun getMnemonic(color: Color) =
    when (color) {
        Color.RED -> "Richard"
        Color.ORANGE -> "Of"
        Color.YELLOW -> "York"
        Color.GREEN -> "Gave"
        Color.BLUE -> "Battle"
        Color.INDIGO -> "In"
        Color.VIOLET -> "Vain"
    }

fun getWarmth(color: Color) = when(color) {
    Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
    Color.GREEN -> "neutral"
    Color.BLUE, Color.INDIGO, Color.VIOLET -> "cold"
}

fun mix(c1: Color, c2: Color) =
    when(setOf(c1, c2)) {
        setOf(Color.RED, Color.YELLOW) -> Color.ORANGE
        setOf(Color.YELLOW, Color.BLUE) -> Color.GREEN
        setOf(Color.BLUE, Color.VIOLET) -> Color.INDIGO
        else -> throw Exception("Dirty color")
    }

fun mixOptimized(c1: Color, c2: Color) = when {
    (c1 == Color.RED && c2 == Color.YELLOW) ||
            (c1 == Color.YELLOW && c2 == Color.RED) -> Color.ORANGE
    (c1 == Color.YELLOW && c2 == Color.BLUE) ||
            (c2 == Color.YELLOW && c1 == Color.BLUE) -> Color.GREEN
    (c1 == Color.BLUE && c2 == Color.VIOLET) ||
            (c1 == Color.VIOLET && c2 == Color.BLUE) -> Color.INDIGO
    else -> throw Exception("Dirty color")
}

interface Expr
class Num(val value: Int) : Expr
class Sum(val left : Expr, val right: Expr): Expr

fun eval1(e: Expr): Int {
    // 1차
    if (e is Num) {
        val n = e as Num
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left)
    }

    throw IllegalArgumentException("Unknown expression")
}

fun eval(e: Expr) : Int =
    // 2차
//    if (e is Num) e.value
//    else if (e is Sum) eval(e.right) + eval(e.left)
//    else throw IllegalArgumentException("Unknown expression")
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throw IllegalArgumentException("Unknown expression")
    }

fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("Num : ${e.value}")
            e.value
        }
        is Sum -> {
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("Sum : ${left} + ${right}")
            left + right
        }
        else -> throw IllegalArgumentException("Unknown expression")
    }

fun fizzBuzz(i : Int) = when {
    i % 15 == 0 -> "FizzBuzz "
    i % 3 == 0 -> "Fizz "
    i % 5 == 0 -> "Buzz "
    else -> "$i "
}

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'

fun recognize(c: Char) = when (c) {
    in '0'..'9' -> "It's a digit!"
    in 'a'..'z', in 'A'..'Z' -> "It's a letter!"
    else -> "I don't know"
}