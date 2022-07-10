package com.example.kotlinexam.chapter9

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.Comparator
import kotlin.reflect.KClass

class VarianceTests {
    @Test
    fun test() {
        val strings = mutableListOf("abc", "bac")
//        addAnswer(strings) // 컴파일 되지 않음
        println(strings.maxByOrNull { it.length })
    }

    @Test
    fun test2() {
        val anyComparator = Comparator<Any> {
            e1, e2 -> e1.hashCode() - e2.hashCode()
        }
        val strings: List<String> = listOf("a", "b")
        strings.sortedWith(anyComparator)

        // Comsumer<T>를 예로 들면 B가 A의 하위타입인 경우 Consumer<A>가 Consumer<B>의 하위 타입인 관계가 성립하면
        // 제네릭 클래스 Consumer<T>는 타입 인자 T에 대해 반공변
        // A와 B의 위치가 서로 뒤바뀐다는 점에 유의
        // ex) Consumer<Animal>은 Consumer<Cat>의 하위 타입
        // Producer와 Consumer는 완전 반대라는 점
    }

    @Test
    fun test3() {
        val ints = mutableListOf(1,2,3)
        val anyItems = mutableListOf<Any>()
        copyData2(ints, anyItems) // Int가 Any의 하위 타입이므로 이 함수를 호출할 수 있다.
    }

    @Test
    fun test4() {
        val list: MutableList<out Number>
//        list.add(42) // 컴파일 에러, 타입 파라미터 T를 함수 인자 타입(in 위치에 있는) 으로 사용하지 못하게 막음
    }

    @Test
    fun test5() {
        // 스타 프로젝션 : 타입 인자 대신 * 사용
        // MutableList<*> != MutableList<Any?>
        // MutableList<Any?>는 모든 타입의 원소를 담을 수 있다
        // MutableList<*>는 그 리스트가 String 같은 구체적인 타입 원소를 저장하기 위해 만들어진 것이라는 뜻, 그러나 그 구체적인 타입 원소는 정확히 모른다는 뜻
        // Any?는 모든 타입의 상위 타입이다.
        val list: MutableList<Any?> = mutableListOf('a', 1, "qwe")
        val chars = mutableListOf('a', 'b', 'c')
        val unknownElements: MutableList<*> = if (Random().nextBoolean()) list else chars
//        unknownElements.add(42) // 컴파일러는 이 메소드 호출을 금지함
        println(unknownElements.first()) // 원소를 가져와도 안전, first()는 Any? 타입의 원소를 반환

        // MutableList<*>는 MutableList<out Any?> 처럼 동작함
        // 안전하게 get으로 가져올 수 있지만, add로 넣을순 없음

        fun printFirst(list: List<*>) {
            if (list.isNotEmpty()) {
                println(list.first())
            }
        }

        printFirst(listOf("Svetlana", "Dmitry"))

        // * 가 더 간결하지만, 제네릭 타입 파라미터가 어떤 타입인지 굳이 알 필요가 없을 때만 사용할 수 있다
        fun <T> printFirst2(list: List<T>) {
            if (list.isNotEmpty()) {
                println(list.first())
            }
        }
    }

    @Test
    fun test6() {
        val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()
        validators[String::class] = DefaultStringValidator
        validators[Int::class] = DefaultIntValidator

        // String 타입의필드를 FieldValidator<*> 타입 검증기로 검증할 수 없다.
//        validators[String::class]!!.validate("")  // => 에러 발생, 알 수 없는 타입의 검증기에 구체적인 타입의 값을 넘기면 안전하지 못하다는 뜻
        val stringValidator = validators[String::class] as FieldValidator<String>
        println(stringValidator.validate("")) // 캐스팅 할 경우 사용 가능

        // 이런 케이스도 컴파일은 되고 검증기 사용할 때 오류가 발생
        val stringValidator2 = validators[Int::class] as FieldValidator<String>
        println(stringValidator2.validate("")) // 여기서 오류 발생함!!
    }

    @Test
    fun test7() {
        // 검증기 캡슐화 테스트
        Validators.registerValidator(String::class, DefaultStringValidator)
        Validators.registerValidator(Int::class, DefaultIntValidator)
        println(Validators[String::class].validate("Kotlin"))
        println(Validators[Int::class].validate(42))

//        println(Validators[String::class].validate(42)) // 에러 발생
    }

}

// 변성: List<String> List<Any>와 같이 기저 타입이 같고 타입 인자가 다른 여러 타입이 서로 어떤 관계가 있는지 설명하는 개념
// List<Any>타입의 파라미터를 받는 함수에 List<String>을 넘기면 안전할까?
// ㄴ String 클래스는 Any를 확장하므로 Any 타입 값을 파라미터로 받는 함수에 String 값을 넘겨도 안전함
// ㄴ 하지만 Any와 String이 List 인터페이스의 타입 인자로 들어가면 자신있게 안전성을 말할 수 없다.
// 이유
fun printContents(list: List<Any>) {
    println(list.joinToString())
}

// 읽기 전용 리스트를 받는다면 구체적인 타입의 원소를 갖는 리스트를 함수에 넘길수 있지만, 리스트가 변경 가능하면 그럴수 없음
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

// var x: String?, var x: String 이 가능한 것부터 코틀린은 둘 이상의 타입을 구성할 수 있다는 뜻
// 타입 사이의 관계를 논하기 위해 하위 타입(subtype)이라는 개념을 잘 알아야 한다.
// 상위 타입(supertype)은 하위 타입의 반대이다.
fun test(i: Int) {
    val n: Number = i
    fun f(s: String) {}
//    f(i) // Int가 String의 하위타입이 아니어서 컴파일 에러
}

// A는 A?의 하위 타입이지만, A?는 A의 하위 타입이 아니다.
// 제네릭 타입을 인스턴스화 할 때 타입 인자로 서로 다른 타입이 들어가면 인스턴스 타입 사이의 하위 타입 관계가 성립하지 않으면 그 제네릭 타입을 무공변(invariant) 이라고 함
// ex) A와 B가 서로 다르기만 하면 MutableList<A>는 항상 MutableList<B>의 하위 타입이 아니다. 자바에서는 모든 클래스가 무공변이다.
// A가 B의 하위 타입이면 List<A>는 List<B>의 하위 타입, 이런 클래스나 인터페이스를 공변적(covariant) 이라고 한다.

// 공변성 : 하위 타입 관계를 유지
// 제네릭 클래스가 타입 파라미터에 대해 공변적임을 표시하려면 타입 파라미터 앞에 out을 넣어야 한다.
interface Producer<out T> {
    fun produce(): T
}

// 무공변 컬렉션 역할을 하는 클래스 정의
open class Animal {
    fun feed() {}
}

// 이 타입 파라미터를 무공변성으로 지정
//class Herd<T: Animal> {
class Herd<out T: Animal> { // 공변성을 위해 변경
    val size: Int get() = 5
    operator fun get(i: Int): T = this[i]
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) {
        animals[i].feed()
    }
}

class Cat: Animal() {
    fun cleanLitter() {

    }
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
    }
    feedAll(cats) // Herd 클래스를 변경해주지 않으면 error -> Type Mismatch
    // Herd 클래스의 T 타입 파라미터에 대해 아무 변성도 지정하지 않아서 고양이 무리는 동물 무리의 하위 클래스가 아님
    // Herd 클래스를 공변적인 클래스로 만들고 호출 코드를 적절히 변경해야 한다.
}

// out 키워드를 붙일 경우 파라미터로 받는 T는 사용하지 못하게 막는다. out은 T의 사용법을 제한하며 T로 생기는 하위 타입 관계의 타입 안정성을 보장함
// 예를 들어 위에 Herd 클래스에서 타입 파라미터 T를 사용하는 장소는 오직 get 메소드의 반환 타입일 뿐

// 공변성 : 하위 타입 관계가 유지된다. Producer<Cat> 은 Producer<Animal>의 하위 타입이다.
// 사용 제한: T를 아웃 위치에서만 사용할 수 있다.
// List<T>의 인터페이스를 보면 List는 읽기 전용, 그 안에 T 타입의 원소를 반환하는 get 메소드는 있지만 기존 값을 변경하는 메소드는 없다.
// ㄴ 따라서 List는 T에 대해 공변적이다.
//interface List<out T>: Collection<T> {
//    operator fun get(index: Int): T // 읽기 전용 메소드로 T를 반환하는 메소드만 정의한다. (따라서 T는 항상 "아웃" 위치에 쓰인다)
//    fun subList(fromIndex: Int, toIndex: Int): List<T> // 여기서도 T는 아웃 위치에 있음
//}

// MutableList<T>를 타입 파라미터 T에 대해 공변적인 클래스로 선언할 수는 없다는 점에 유의
// MutableList<T>에는 T를 인자로 받아 그 타입의 값을 반환하는 메소드가 있음 (T가 인과 아웃 위치에 동시에 쓰임)
//interface MutableList<T>: List<T>, MutableCollection<T> {
//    override fun add(element: T) : Boolean
//}
// MutableList는 T에 대해 공변적일 수 없다. 이유는 T가 "인" 위치에 쓰이기 때문

// 생성자 파라미터는 "인"과 "아웃" 어느 쪽에도 해당하지 않기 때문에 사용할 수 있다.
class Herd2<out T: Animal>(vararg animals: T)

// 그런데 val, var 키워드를 생성자 파라미터에 넣는다면 게터나 세터를 정의하는 것과 같다.
// 읽기 전용 프로퍼티는 아웃 위치, 변경 가능 프로퍼티는 아웃과 인 위치에 모두 해당함
// 여기서는 T 타입인 leadAnimal 프로퍼티가 인 위치에 있기 떄문에 T를 out으로 표시할 수 없음
class Herd3<T: Animal>(val leadAnimal: T, vararg animals: T)

// 반공변성: 뒤집힌 하위 타입 관계
//interface Comparator<in T> {
//    fun compare(e1: T, e2: T): Int = 1 // T를 인 위치에 사용한다.
//}

// 클래스나 인터페이스가 어떤 타입 파라미터에 대해서는 공변적이면서 다른 타입 파라미터에 대해서는 반공변적일 수 있다. Function 인터페이스가 고전적 예
interface Function1<in P, out R> {
    operator fun invoke(p: P): R
}

// 예를 들면 동물을 인자로 받아 정수를 반환하는 람다를 고양이에게 번호를 붙이는 고차 함수에 넘길 수 있다.
fun enumerateCats(f: (Cat) -> Number) {}
fun Animal.getIndex(): Int = 3
// enumberateCats(Animal::getIndex) // Animal은 Cat의 상위 타입이며 Int는 Number의 하위 타입이므로 올바른 코틀린 식임

// 클래스를 선언하며 변성을 지정하면 그 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼쳐서 편리함 -> 선언 지점 변성 (declaration site variance)
// 자바의 와일드 카드 타입 (? extends, ? super)에 익숙하면
// 자바는 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지 명시해야 한다. -> use-site variance (사용 지점 변성)
// 자바는 사용자의 예상대로 작동하는 API를 만들기 위해 라이브러리 개발자는 항상 Function<? super T, ? extends R> 처럼 와일드카드를 사용해야 한다.

// 무공변 파라미터 타입을 사용하는 데이터 복사 함수
// 두 파라미터 컬렉션 다 무공변 타입, 원본에서는 읽기만 하고 대상에는 쓰기만 함
fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

// 타입 파라미터가 둘인 데이터 복사 함수
fun <T: R, R> copyData2(source: MutableList<T>, destination: MutableList<R>) { // source 원소 타입은 destination 원소 타입의 하위 타입이어야 한다.
    for (item in source) {
        destination.add(item)
    }
}

// 아웃-프로젝션 타입 파라미터를 사용하는 데이터 복사 함수
// out 키워드를 타입을 사용하는 위치 앞에 붙이면 T 타입을 "in" 위치에 사용하는 메소드를 호출하지 않는다는 뜻이다
// 이때 타입 프로젝션 (type projection)이 일어나는데 source를 일반적인 MutableList가 아니라 MutableList를 프로젝션 한(제약을 가한) 타입으로 생성
// copyData3은 MutableList의 메소드 중에서 반환 타입으로 타입 파라미터 T를 사용하는 메소드만 호출할 수 있음,
fun <T> copyData3(source: MutableList<out T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

// in 프로젝션 타입 파라미터를 사용하는 데이터 복사 함수
// 원본 리스트 원소 타입의 상위 타입을 대상 리스트 원소 타입으로 허용
fun <T> copyData4(source: MutableList<T>, destination: MutableList<in T>) {
    for (item in source) {
        destination.add(item)
    }
}
// MutableList<out T> = MutableList<? extends T>와 같고 MutableList<in T> = MutableList<? super T>와 같음

// 스타 프로젝션을 쓰는 방법과 사용시 빠지기 쉬운 함정 예제
// 사용자 입력을 검증해야 해서 FieldValidator라는 인터페이스를 정의했다고 가정
// FieldValidator는 반공변성, String 타입의 필드를 검증하기 위해 Any 타입을 검증하는 FieldValidator를 사용할 수 있다.
interface FieldValidator<in T> {
    fun validate(input: T): Boolean // T를 인 위치에서만 사용한다.
}
object DefaultStringValidator: FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}
object DefaultIntValidator: FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}

// 검증기 컬렉션에 대한 접근 캡슐화 하기
object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()
    fun <T: Any> registerValidator(kClass: KClass<T>, fieldValidator: FieldValidator<T>) {
        validators[kClass] = fieldValidator
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(kClass: KClass<T>): FieldValidator<T> =
        validators[kClass] as? FieldValidator<T> ?:
        throw IllegalArgumentException("No validator for ${kClass.simpleName}")
}