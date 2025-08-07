package dev.matvenoid.backend.infrastructure.config

import dev.matvenoid.backend.application.security.UserPrincipal
import dev.matvenoid.backend.application.service.JwtService
import dev.matvenoid.backend.domain.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val userId = jwtService.extractUserId(jwt)

        if (userId != null && SecurityContextHolder.getContext().authentication == null) {
            val user = userRepository.findById(userId)
            if (user != null) {
                val userPrincipal = UserPrincipal(
                    id = user.id,
                    phone = user.phone,
                    passwordHash = user.passwordHash,
                    authoritiesCollection = emptyList()
                )

                if (jwtService.isTokenValid(jwt, userPrincipal)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.authorities
                    )
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
