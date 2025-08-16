package dev.matvenoid.backend.infrastructure.config

import dev.matvenoid.backend.application.security.UserPrincipal
import dev.matvenoid.backend.application.service.JwtService
import dev.matvenoid.backend.application.service.TokenBlacklistService
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
    private val userRepository: UserRepository,
    private val tokenBlacklistService: TokenBlacklistService,
    ) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length < 8) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        if (tokenBlacklistService.isBlacklisted(jwt)) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = jwtService.extractUserId(jwt)
        if (userId == null) {
            filterChain.doFilter(request, response)
            return
        }

        val user = userRepository.findById(userId)
        if (user == null) {
            filterChain.doFilter(request, response)
            return
        }

        val principal = UserPrincipal(
            user.id,
            user.email,
            user.passwordHash,
            emptyList()
        )

        if (!jwtService.isTokenValid(jwt, principal)) {
            filterChain.doFilter(request, response)
            return
        }

        val auth = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}
