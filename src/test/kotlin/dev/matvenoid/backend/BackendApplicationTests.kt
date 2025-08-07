package dev.matvenoid.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
internal class BackendApplicationTests {
    companion object {
        @Container
        var postgresqlContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16")

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add(
                "spring.datasource.url",
                postgresqlContainer::getJdbcUrl
            )
            registry.add(
                "spring.datasource.username",
                postgresqlContainer::getUsername
            )
            registry.add(
                "spring.datasource.password",
                postgresqlContainer::getPassword
            )
        }
    }

    @Test
    fun contextLoads() {
    }
}
