package com.example.kotlinexam.chapter3

import org.junit.jupiter.api.Test

class CollectionTests {
    @Test
    fun test() {
        val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")
        1.to("one") // 일반적인 방법으로 to 함수 호출
        val (number, name) = 1 to "one" // 중위 호출(infix call)로 함수 호출

        println("12.345-6.A".split(".", "-"))

        parsePath("/Users/yole/kotlin-book/chapter.adoc")
        parsePathRegex("/Users/yole/kotlin-book/chapter.adoc")

        // 여러 줄 3중 따옴표
        val kotlinLogo = """|   //
                           .|   //
                           .|/ \""".trimMargin()
        println(kotlinLogo)
    }

    // to의 간략한 함수 정의
    // infix fun Any.to(other: Any) = Pair(this, other)

    // vararg : 가변 파라미터 인자를 넘길 때 사용 (자바에 ...)

    fun parsePath(path: String) {
        val directory = path.substringBeforeLast("/")
        val fullName = path.substringAfterLast("/")
        val fileName = fullName.substringBeforeLast(".")
        val extension = fullName.substringAfterLast(".")

        println("Dir : $directory, name: $fileName, ext: $extension")
    }

    fun parsePathRegex(path: String) {
        val regex = """(.+)/(.+)\.(.+)""".toRegex()
        val matchResult = regex.matchEntire(path)

        if (matchResult != null) {
            val (directory, filename, extension) = matchResult.destructured
            println("Dir : $directory, name: $filename, ext: $extension")
        }
    }
}