package com.example.kotlinexam.chapter4

import com.google.gson.Gson
import org.junit.jupiter.api.Test
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File

// equals, hashCode, toString함수를 구현해줄 필요 없이 자동으로 만들어줌
class DataClassTests {
    @Test
    fun test() {
//        val client1 = Client("오현석", 4122)
//        val client2 = Client("오현석", 4122)
//        println(client1)
//
//        println(client1 == client2)

        val client3 = Client2("예에", 222)
        val client4 = client3.copy()

        println(client3 == client4)

        val cset = CountingSet<Int>()
        cset.addAll(listOf(1,1,2))

        println("${cset.objectsAdded} objects were added, ${cset.size} remain ")
    }

    @Test
    fun test2() {
        val persons = listOf(Person("Bob"), Person("Alice"))
        println(persons.sortedWith(Person.NameComparator))
    }

    @Test
    fun companionTest() {
        A.bar()

        val person = Person2.Loader.fromJSON("{name: 'Dmitry'}")
        println(person.name)

        val person2 = Person2.fromJSON("{name: 'Brent'}")
        println(person2.name)
    }
}

class Client(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Client)
            return false

        return name == other.name && postalCode == other.postalCode
    }
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode

    // copy 메소드 구현
    fun copy(name: String = this.name, postalCode: Int = this.postalCode) =
        Client(name, postalCode)
}

// data class 로 선언하기 data class에는 hashcode, equals, toString 함수가 포함되어 있음
data class Client2(val name: String, val postalCode: Int)

// by 키워드 사용하기 (상속을 허용하지 않는 클래스에 새로운 동작을 추가해야 할 때가 있을 때 데코레이터 패턴을 사용함
// 데코레이터 패턴은 기존 기능이 그대로 필요한 경우 기존 클래스의 메소드에게 요청을 전달, 새로 정의해야하는 기능은 메소드를 새로 정의
// Collection 같이 단순한 인터페이스를 구현하면서 아무 동작도 변경하지 않는 데코레이터는 아래와 같이 구현
class DelegatingCollection<T>: Collection<T> {
    private val innerList = arrayListOf<T>()
    override val size: Int get() = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun contains(element: T): Boolean = innerList.contains(element)
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean =
        innerList.containsAll(elements)
}

// by 키워드를 사용해 재구성함
class DelegatingCollection2<T>(innerList: Collection<T> = arrayListOf<T>())
    : Collection<T> by innerList {}

// 원소를 추가할 경우 횟수 기록하는 컬렉션
class CountingSet<T>(
    val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet {
    var objectsAdded = 0
    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }
    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}

// object 키워드 : 클래스 선언과 인스턴스 생성
// 싱글턴 패턴을 구현할 때
object Payroll {
    val allEmployees = arrayListOf<Person>()
    fun calculateSalary() {
        for (person in allEmployees) {

        }
    }
}

// Comparator 구현
object CaseInsensitiveFileComparator: Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path, ignoreCase = true)
    }
}

// 중첩 객체를 사용해 Comparator 구현
data class Person(val name: String) {
    object NameComparator: Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int =
            p1.name.compareTo(p2.name)
    }
}

// 코틀린에는 static이 없음, 동반 객체라는 개념이 있음 -> companion 이라는 표시를 붙이면 됨
class A {
    companion object {
        fun bar() {
            println("Companion object called")
        }
    }
}

// 팩터리 패턴으로 companion 표시 이용하기
class UserCompanion private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) =
            UserCompanion(email.substringBefore("@"))
        fun newFaceboolUser(accountId: Int) =
            UserCompanion(accountId.toString())
    }
}

// 동반 객체를 일반 객체처럼 이용하기
class Person2(val name: String) {
    companion object Loader {
        val gson: Gson = Gson()
        fun fromJSON(jsonText: String): Person2 = gson.fromJson(jsonText, Person2::class.java)
    }
}

// 동반 객체에서 인터페이스 구현하기
interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class Person3(val name: String) {
    companion object: JSONFactory<Person3> {
        val gson: Gson = Gson()
        override fun fromJSON(jsonText: String): Person3 = gson.fromJson(jsonText, Person3::class.java)
    }
}
// 동반 객체의 인스턴스를 함수에 넘김
//fun loadFromJSON<T>(factory: JSONFactory<T>): T {
//    ...
//}

// 동반 객체에 대한 확장 함수 정의
class Person4(val firstName: String, val lastName: String) {
    companion object { // 비어있는 동반 객체 선언
    }
}
// 확장 함수를 선언한다.
fun Person4.Companion.fromJSON(json: String): Person4 {
    val gson: Gson = Gson()
    return gson.fromJson(json, Person4::class.java)
}

// 무명 내부 클래스를 다른 방식으로 작성
//window.addMouseListener(
//    object: MouseAdapter() { // 무명 객체를 선언
//        override fun mouseClicked(e: MouseEvent) {
//            //
//        }
//        override fun mouseEntered(e: MouseEvent) {
//            //
//        }
//    }
//)

// 무명 객체 안에서 로컬 변수 사용
fun countClicks(window: Window) {
    var clickCount = 0
    window.addMouseListener(object: MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            clickCount ++
            super.mouseClicked(e)
        }
    })
}