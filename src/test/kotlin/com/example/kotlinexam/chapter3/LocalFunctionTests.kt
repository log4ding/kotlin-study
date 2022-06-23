package com.example.kotlinexam.chapter3

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class LocalFunctionTests {
    @Test
    fun test() {
        saveUser(User(1, "", ""))
    }
}

class User(val id: Int, val name: String, val address: String)
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException(
                "Can't save user ${this.id}: empty $fieldName"
            )
        }
    }

    validate(this.name, "Name")
    validate(this.address, "Address")
}

fun saveUser(user: User) {
    // local function
    user.validateBeforeSave()
    // save user
}