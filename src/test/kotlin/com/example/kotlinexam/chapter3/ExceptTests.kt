package com.example.kotlinexam.chapter3

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.StringReader
import java.lang.NumberFormatException

class ExceptTests {
    @Test
    fun test() {
        val reader = BufferedReader(StringReader("abc"))
        println(readNumber(reader))
    }

    fun readNumber1(reader: BufferedReader): Int? {
        try {
            val line = reader.readLine()
            return Integer.parseInt(line)
        } catch(e: NumberFormatException) {
            return null
        }
        finally {
            reader.close()
        }
    }

    fun readNumber(reader: BufferedReader) {
        val number = try {
            Integer.parseInt(reader.readLine())
        } catch (e: NumberFormatException) {
//            return
            null // return도 가능, null도 가능
        }
        println(number)
    }
}