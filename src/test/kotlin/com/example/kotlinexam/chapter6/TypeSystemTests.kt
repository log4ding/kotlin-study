package com.example.kotlinexam.chapter6

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TypeSystemTests {
    private lateinit var myService: MyService
    @BeforeEach
    fun setUp() {
        myService = MyService()
    }

    @Test
    fun testAction() {
        // junit 4 에서 동작하지 않음 -> Kotlin Junit5로 변경해서 실행
        Assertions.assertEquals("foo", myService.performAction())
    }

    @Test
    fun test1() {
        printAllCaps("English")

        val ceo = Employee("Da Boss", null)
        val developer = Employee("Bob Smith", ceo)
        println(manageName(developer))
        println(manageName(ceo))
    }

    @Test
    fun test2() {
        val person = Person("Dmitry", null)
        println(person.countryName())
    }

    @Test
    fun test3() {
        val address = Address("Elsestr. 47", 80687, "Munich", "Germany")
        val company = Company("Jetbrains", address)
        printShippingLabel(Person("Dmitry", company))
    }

    @Test
    fun test4() {
        val p1 = Person2("Dmitry", "Jemerov")
        val p2 = Person2("Dmitry", "Jemerov")
        println(p1 == p2)
    }

    @Test
    fun test5() {
        ignoreNulls(null) // NullPointException 발생
    }

    @Test
    fun test6() {
        // let
        var email: String? = "yole@example.com"
        // null이 아니기 때문에 실행됨
        email?.let{ sendEmailTo(it) }

        email = null
        // null이라서 실행되지 않음
        email?.let{ sendEmailTo(it) }
    }

    @Test
    fun test7() {
        verifyUserInput(" ")
        verifyUserInput(null) // isNullOrBlank 에 null 을 전달해도 예외가 발생하지 않음
    }

    @Test
    fun test8() {
        val x = 1
        val list = listOf(1L, 2L, 3L)
//        println(x in list) // 허용되지 않음
        println(x.toLong() in list)

        println("42".toInt()) // 문자열 -> 숫자 변환
    }
}

// ? 기호로 Nullable인지 표현해줌, Nullable 에는 바로 length 같은 함수를 작성할 수 없다.
fun strLenSafe(s: String?) = s?.length ?: 0

fun printAllCaps(s: String?) {
    val allCaps: String? = s?.uppercase()
    println(allCaps)
}


// 널이 될 수 있는 프로퍼티를 다루기 위해 안전한 호출
class Employee(val name: String, val manager: Employee?)
fun manageName(employee: Employee): String? = employee.manager?.name

// 안전한 호출 연쇄시키기
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName1(): String {
    val country = this.company?.address?.country
    return country ?: "Unknown"
}

// 엘비스(elvis) 연산자 ?: = if (s != null) s else ""
fun foo(s: String) {
    val t: String = s ?: ""
}
// 엘비스 연산자를 통해 한 줄로 표현
fun Person.countryName(): String = company?.address?.country ?: "Unknown"

// throw와 엘비스 연산자 함께 사용하기
fun printShippingLabel(person: Person) {
    val address = person.company?.address ?: throw IllegalArgumentException("No Address")
    with(address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

// 안전한 캐스트 as?
class Person2(private val firstName: String, private val lastName: String) {
    override fun equals(o: Any?): Boolean {
        val otherPerson = o as? Person2 ?: return false
        return otherPerson.firstName == firstName &&
                otherPerson.lastName == lastName
    }

    override fun hashCode(): Int = firstName.hashCode() * 37 + lastName.hashCode()
}

// 널 아님 단언 !! -> 되도록 사용하지 말라고 기호를 느낌표 두 개로 사용했음
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!
    println(sNotNull.length)
}

// let 함수 -> 함수 파라미터에 널이 아닌 값이 들어왔을 때만 수행할 수 있다
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

// 나중에 초기화 할 프로퍼티 (JUnit에 @Before, onCreate 같은 함수)
class MyService {
    fun performAction(): String = "foo"
}

// 안전한 호출을 하지 않아도 되는 메소드가 있다
fun verifyUserInput(input: String?) {
    if (input.isNullOrBlank()) {
        println("Please fill in the required fields")
    }
}

fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank() // 두 번째 this에는 스마트 캐스트가 적용됨


// 널이 될 수 있는 원시 타입
data class Person3(val name: String, val age: Int? = null) {
    fun isOlderThan(other: Person3): Boolean? {
        if (age == null || other.age == null) return null
        return age > other.age
    }
}

// 숫자 변환
val i = 1
//val l: Long = i // 컴파일 오류 발생
val l: Long = i.toLong()

// Any, Unit, Nothing
// Any - 최상위 타입
val answer: Any = 42
// Any 타입은 Object 타입과 대응한다. Any는 자바 바이트 코드의 Object로 컴파일 된다.

// Unit = void
fun f() : Unit {}
fun f2() {}

// Void와 차이점 (인터페이스를 구현할 때 리턴을 무언가 해야하는데, 반환값이 없을 경우 Unit을 써주면 리턴하지 않아도 컴파일러가 Unit을 리턴하는 걸 넣어줌)
interface Processor<T> {
    fun process(): T
}

class NoResultProcessor: Processor<Unit> {
    override fun process() {}
}

// Nothing
fun fail(message: String): Nothing {
    throw java.lang.IllegalArgumentException(message)
}

// 반환 값이 쓰이지 않음,
val company = Company("카카오", Address("경기도 고양시 일산동구", 16540, "", ""))
val address = company.address ?: fail("No Success")
