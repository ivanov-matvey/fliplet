package dev.matvenoid.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
internal class BackendApplicationTests {
    companion object {
        @Container
        var postgresqlContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17")

        @Container
        val redis = GenericContainer(DockerImageName.parse("redis:8"))
            .withExposedPorts(6379)
            .withCommand("--notify-keyspace-events", "Ex")

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
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @Test
    fun contextLoads() {
    }
}
