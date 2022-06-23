package com.example.kotlinexam.chapter7

import org.junit.jupiter.api.Test

class PropertyTests {
    @Test
    fun test() {
        val p = Person9()
        val data = mapOf("name" to "Dmitry", "company" to "JetBrains")
        for ((attrName, value) in data) {
            p.setAttribute(attrName, value)
        }

        println(p.name)
    }
}

class Person9 {
    private val _attributes = hashMapOf<String, String>()
    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    // 필수 정보
    val name: String
    get() = _attributes["name"]!!
}

class Person10 {
    private val _attributes = hashMapOf<String, String>()
    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }
    val name: String by _attributes // 위임 프로퍼티로 맵을 사용
}

//object Users: IdTable() {
//    val name = varchar("name", length=50).index()
//    val age = integer("age")
//}
//
//class User(id: EntityID): Entity(id) {
//    var name: String by Users.name // Users.name은 "name" 프로퍼티에 해당하는 위임 객체
//    var age: Int by Users.age
//}