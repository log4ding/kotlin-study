package com.example.kotlinexam.ddd

import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class Chapter7 {
    @Test
    fun test() {
//        ServiceLocator.register(IRepository::class.java, TestRepository())
        ServiceLocator.register(IRepository::class.java, InmeoryRepository())
        val userRepository = ServiceLocator.resolve(IRepository::class.java)
    }

    @Test
    fun test2() {
        val userApplicationService = AnnotationConfigApplicationContext(AppConfig::class.java).use { context ->
            context.getBean(UserApplicationService::class.java)
        }
    }
}

interface IRepository {
    fun save(user: User)
    fun findById(id: String): User?
}

class TestRepository : IRepository {
    override fun save(user: User) {
        println("save")
    }

    override fun findById(id: String): User? {
        return null
    }
}

class InmeoryRepository : IRepository {
    override fun save(user: User) {
        println("save")
    }

    override fun findById(id: String): User? {
        return null
    }

}

object ServiceLocator {
    private val services = mutableMapOf<Class<*>, IRepository>()

    fun <T : IRepository> register(serviceClass: Class<T>, service: T) {
        services[serviceClass] = service
    }

    fun <T : IRepository> resolve(serviceClass: Class<T>): T {
        return services[serviceClass] as T
    }
}

@Configuration
class AppConfig {
    @Bean
    fun userRepository(): IRepository {
        return TestRepository()
    }

    @Bean
    fun userApplicationService(userRepository: IRepository): UserApplicationService {
        return UserApplicationService(userRepository)
    }
}