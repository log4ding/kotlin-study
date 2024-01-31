package com.example.kotlinexam.ddd

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.TestPropertySource
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.Id

@DataJpaTest
@TestPropertySource(locations = ["classpath:application.properties"])
class Chapter5 {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun test() {
        val newUser = UserEntity().also {
            it.name = UserName("john")
        }

        userRepository.save(newUser)

        val user = userRepository.findByName("john")
        val user2 = userRepository.findById("1234")
        println(user)
        println(user2)
    }
}

interface UserRepository : CrudRepository<UserEntity, UserId> {
    fun findByName(name: String): UserEntity?
    fun findById(id: String): UserEntity?
}

@Entity(name = "user")
class UserEntity {
    @Id
    var id: UserId = UserId(UUID.randomUUID().toString())

    @Column(name = "name")
    var name: UserName? = null
}