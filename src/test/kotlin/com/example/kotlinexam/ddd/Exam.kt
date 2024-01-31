package com.example.kotlinexam.ddd

class Exam {
}

class LowCohesion(
    private val v1: Int,
    private val v2: Int,
    private val v3: Int,
    private val v4: Int,
) {
    fun methodA(): Int {
        return v1 + v2
    }

    fun methodB(): Int {
        return v3 + v4
    }
}

class HighCohesionA(
    private val v1: Int,
    private val v2: Int,
) {
    fun methodA(): Int {
        return v1 + v2
    }
}

class HighCohesionB(
    private val v3: Int,
    private val v4: Int,
) {
    fun methodA(): Int {
        return v3 + v4
    }
}

class ObjectA(
    private val objectB: ObjectB
) {
}

class ObjectB {

}