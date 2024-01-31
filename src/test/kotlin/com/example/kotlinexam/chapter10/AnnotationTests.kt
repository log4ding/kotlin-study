package com.example.kotlinexam.chapter10

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Test
import java.lang.reflect.InvocationTargetException
import kotlin.io.path.createTempDirectory
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KFunction2
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

class AnnotationTests {
//    @TempDir
//    val folder: Path = TODO()

    @Test
    fun test() {
        val createFile = kotlin.io.path.createTempFile("myFile.txt")
        val createFolder = createTempDirectory("subfolder")
    }

    fun test(list: List<*>) {
        // 안전하지 못한 캐스팅 경고를 무시하는 로컬 변수 선언
        @Suppress("UNCHECKED_CAST")
        val strings = list as List<String>
    }

    fun testInvokeFunc() : Unit {
        println("test test!")
    }


    @Test
    fun test2() {
        val gson = Gson()
        data class Person(
            @Expose
            val name: String,
            val age: Int
        )
        val person = Person("Alice", 20)
        println(gson.toJson(person))

        // deserialize
        val json = """{"name":"Alice", "age": 28}"""
        println(gson.fromJson(json, Person::class.java))
    }

    @Test
    fun test3() {
        data class Person(
            @Expose
            val name: String,
            val age: Int
        )

        val person = Person("Alice", 29)
        val kClass = person.javaClass.kotlin
        println(kClass.simpleName)

        kClass.memberProperties.forEach { println(it.name) }
    }

    fun foo(x: Int) = println(x)
    fun sum(x: Int, y: Int) = x + y
    @Test
    fun test4() {
        // KFunction의 인스턴스
        val kFunction = ::foo
        kFunction.call(42)
    }

    @Test
    fun test5() {
        // KFunction
        val kFunction: KFunction2<Int, Int, Int> = ::sum
        println(kFunction.invoke(1, 2 ) + kFunction(3,4))

        // KProperty -> 동작하지 않음
//        var counter = 0
//        val kProperty = ::counter
//        kProperty.setter.call(21)
//        println(kProperty.get())
    }

    @Test
    fun test6() {
        val testString = "ab dc"
        println(StringUtils.trimToEmpty(testString))
    }

    @Test
    fun test7() {
        invokeMethod("testInvokeFunc")
    }
}

fun Any.invokeMethod(name: String): Any {
    val method = this::class.java.getDeclaredMethod(name)
    try {
        method.isAccessible = true
        return method.invoke(this)
    } catch (e: InvocationTargetException) {
        throw e.cause!!
    }
}

// 애노테이션에 배열을 인자로 지정하는 방법
//@RequestMapping(path = arrayOf("/foo", "/bar"))

// 애노테이션 인자를 컴파일 시점에 알 수 있어야 한다. 프로퍼티를 애노테이션 인자로 사용하려면 const 변경자를 붙인다.
//const val TEST_TIMEOUT = 100L
//@Test(timeout= TEST_TIMEOUT)
//fun testMethod() {}

// 애노테이션 대상
// 사용 지점 대상 (use-site target) 선언으로 애노테이션을 붙일 요소를 정할 수 있다.
// 지점 대상은 @기호와 애노테이션 이름 사이에 붙음 애노테이션 이름과는 콜론(:)으로 분리됨
// ex) @get:Rule

// @JvmField 게터나 세터가 없는 공개된(public) 자바 필드로 프로퍼티를 노출시킴
// @JvmName은 코틀린 선언이 만들어내는 자바 필드나 메소드 이름을 변경
// @JvmStatic 은 메소드, 객체 선언, 동반 객체에 적용하면 그 요소가 자바 정적 메소드로 노출됨
// @JvmOverloads 를 사용하면 디폴트 파라미터 값이 있는 함수에 대해 컴파일러가 자동으로 오버로딩한 함수를 생성해줌


// 코틀린 메타 애노테이션
// 애노테이션 클래스에 붙일수 있는 애노테이션을 메타애노테이션이라고 함
// @Target 애노테이션
// Target은 애노테이션을 적용할 수 있는 요소의 유형을 지정, AnnotationTarget 을 설정

// @Retention 애노테이션
// 정의 중인 애노테이션 클래스를 소스 수준에서만 유지할지, .class 파일에 저장할지, 실행 시점에 리플렉션을 사용해 접근할 수 있게 할지를 지정하는 메타 애노테이션
// 기본적으로 자바는 .class 파일에 저장하지만 런타임에서는 사용할 수 없게 한다. 보통 애노테이션은 런타임에 사용할 수 있게 해야하기 때문에 Retention을 RUMTIME으로 지정함
// 코틀린은 기본적으로 Retention을 RUNTIME으로 지정한다.


// jkid 라이브러리엔 역직렬화를 제어할 때 쓰는 애노테이션이 있는데 @DeserializeInterface
// Gson 에서는 Adaptor를 통해 제어하고 있다.
// 코틀린에서는 annotation class DeserializeInterface(val targetClass: KClass<out Any>)
// 이런식으로 사용함 클래스를 참조할 수 있도록 되어 있음
// KClass 타입 파라미터를 쓸 때 out 없이 쓰면 CompanyImpl::class를 인자로 넘길수 없고 Any::class만 넘길수 있다.
// 반면 out 키워드가 있다면 Any를 확장하는 모든 클래스에 대한 참조를 전달할 수 있다.

// 애노테이션 파라미터로 제네릭 클래스 받기
//annotation class CustomSerializer(
//    val seializerClass: KClass<out ValueSerializer<*>>
//)
// CustomSerializer는 ValueSerializer를 구현하는 클래스만 인자로 받아야 한다.

// 리플렉션 : 실행 시점에 코틀린 객체 내부 관찰
// 리플렉션이란 ? 실행 시점에 객체의 프로퍼티와 메소드에 접근할 수 있게 해주는 방법
// 코틀린의 리플렉션 API : KClass, KCallable, KFunction, KProperty
// KClass -> MyClass::class 라고 얻으면 인스턴스를 얻을 수 있다.

// KClass 내부
//interface KClass<T: Any> {
//    val simpleName: String?
//    val qualifiedName: String?
//    val members: Collection<KCallable<*>>
//    val constructors: Collection<KFunction<T>>
//    val nestedClasses: Collection<KClass<*>>
//}

// KCallable
//interface KCallable<out R> {
//    fun call(vararg args: Any?): R
//}