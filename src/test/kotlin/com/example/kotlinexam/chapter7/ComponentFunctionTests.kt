package com.example.kotlinexam.chapter7

import org.junit.jupiter.api.Test
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.lang.reflect.Type
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class ComponentFunctionTests {
    @Test
    fun test() {
        val (name, ext) = splitFileName("example.kt")
        println(name)
        println(ext)
    }

    @Test
    fun test2() {
        val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
        printEntries(map)

        // map entry는 componentN 함수를 제공함
        for (entry in map.entries) {
            val key = entry.component1()
            val value = entry.component2()
        }
    }

    @Test
    fun test3() {
        val p = Person6("Dmitry", 34, 2000)
        p.addPropertyChangeListener(
            PropertyChangeListener { event ->
                println("Property ${event.propertyName} changed " + "from ${event.oldValue} to ${event.newValue}")
            }
        )
        p.age = 35
        p.salary = 2100
    }
}

// 구조 분해 사용하기
class Point3(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

data class NameComponents(val name: String, val extension: String)

fun splitFileName(fullName: String): NameComponents {
    val result = fullName.split('.', limit = 2)
    return NameComponents(result[0], result[1])
}

// 구조 분해 선언을 사용해 맵 이터레이터
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

// 위임 프로퍼티
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}

class Foo {
    private val delegate = Delegate()
//    var p: Type by Delegate()
}

class Person4(val name: String) {
    private var _emails: List<String>? = null
    val emails: List<String>
        get() {
            if (_emails == null) {
                _emails = listOf("none")
            }
            return _emails!!
        }
}

// 지연 초기화를 위임 프로퍼티를 통해 구현
class Person5(val name: String) {
    val emails by lazy { listOf("None") }
}

// PropertyChangeSupport 를 사용하기 위한 도우미 클래스
open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}

// 프로퍼티 변경 통지를 직접 구현하기
class Person6(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int = age
        set(newValue) {
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("age", oldValue, newValue)
        }
    var salary: Int = salary
        set(newValue) {
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("salary", oldValue, newValue)
        }
}

// 도우미 클래스를 통해 프로피터 변경 통지 구현하기
//class ObservableProperty(val propName: String,
//                          var propValue: Int,
//                          val changeSupport: PropertyChangeSupport) {
//    fun getValue(): Int = propValue
//    fun setValue(newValue: Int) {
//        val oldValue = propValue
//        propValue = newValue
//        changeSupport.firePropertyChange(propName, oldValue, newValue)
//    }
//}
//class Person7(val name: String, age: Int, salary: Int): PropertyChangeAware() {
//    val _age = ObservableProperty("age", age, changeSupport)
//    var age: Int
//        get() = _age.getValue()
//        set(value) { _age.setValue(value) }
//    val _salary = ObservableProperty("salary", salary, changeSupport)
//    var salary: Int
//        get() = _salary.getValue()
//        set(value) { _salary.setValue(value) }
//}

// 위에 코드에서 한단계 더 간단하게 구현
class ObservableProperty(var propValue: Int, val changeSupport: PropertyChangeSupport) {
    operator fun getValue(p: Person7, prop: KProperty<*>): Int = propValue
    operator fun setValue(p: Person7, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}

class Person7(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int by ObservableProperty(age, changeSupport)
    var salary: Int by ObservableProperty(salary, changeSupport)
}

// Delegates.observable을 사용해 프로퍼티 변경 통지 구하기
class Person8(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    private val observer = { prop: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
    var age: Int by Delegates.observable(age, observer)
    var salary: Int by Delegates.observable(salary, observer)
}

