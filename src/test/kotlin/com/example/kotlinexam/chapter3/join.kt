package com.example.kotlinexam.chapter3

// Collection<T>를 추가해 확장 함수로 선언
fun <T> Collection<T>.joinToString(
    separator: String = ", ",   // default 값 지정
    prefix: String = "",        // default 값 지정
    postfix: String = ""        // default 값 지정
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

// 문자열의 컬렉션에 대해서만 호출할 수 있는 join 함수 구현
fun Collection<String>.join(
    separator: String = ", ",   // default 값 지정
    prefix: String = "",        // default 값 지정
    postfix: String = ""        // default 값 지정
) = joinToString(separator, prefix, postfix)

// extension function
//fun String.lastChar() : Char = this.get(this.length - 1)
// extension value
val String.lastChar: Char
    get() = get(length - 1)

var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value: Char) {
        this.setCharAt(length -1, value)
    }