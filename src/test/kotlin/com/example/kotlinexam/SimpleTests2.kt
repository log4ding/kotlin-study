package com.example.kotlinexam

import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import java.time.Clock
import java.time.Duration
import java.time.Instant

class SimpleTests2 {

    fun f1(name: String, age: Int = 30) {
        println("name = $name, age = $age")
    }

    @Test
    fun test() {
        f1("lu.j")

        f1(name = "lu.j", age = 35)
    }

    fun call(before: () -> Unit = {}, after: () -> Unit = {}) {
        before()
        print("MIDDLE")
        after()
    }

    @Test
    fun test2() {
//        call({ print("CALL") }) // CALLMIDDLE
        call({ print("CALL") }, { print("AFTER") })
        call(after = { print("AFTER") })
//        call { println("CALL") }  // MIDDLECALL
    }


    @Test
    fun test3() {
        val now = Clock.systemDefaultZone().millis() / 1000
        val windowSize = Duration.ofHours(1)
        val calcSize = ((now / windowSize.seconds) + 1) * windowSize.seconds

        println(Instant.ofEpochMilli(calcSize * 1000))
    }

}