package dev.matvenoid.backend.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import java.nio.charset.StandardCharsets

@Configuration
class SecurityErrorHandlersConfig(
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun customAuthenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { request, response, _ ->
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()

            val body = mapOf(
                "type" to "/errors/unauthorized",
                "title" to "Unauthorized",
                "status" to HttpStatus.UNAUTHORIZED.value(),
                "detail" to "Full authentication is required to access this resource",
                "instance" to request.requestURI
            )
            response.writer.write(objectMapper.writeValueAsString(body))
        }
    }

    @Bean
    fun customAccessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { request, response, _ ->
            response.status = HttpStatus.FORBIDDEN.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()

            val body = mapOf(
                "type" to "/errors/forbidden",
                "title" to "Forbidden",
                "status" to HttpStatus.FORBIDDEN.value(),
                "detail" to "Access denied",
                "instance" to request.requestURI
            )
            response.writer.write(objectMapper.writeValueAsString(body))
        }
    }
}
