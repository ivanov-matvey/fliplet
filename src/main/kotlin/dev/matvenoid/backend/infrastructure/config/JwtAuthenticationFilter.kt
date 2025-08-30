package dev.matvenoid.backend.infrastructure.config

import dev.matvenoid.backend.application.service.JwtService
import dev.matvenoid.backend.application.service.TokenBlacklistService
import dev.matvenoid.backend.domain.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val tokenBlacklistService: TokenBlacklistService,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length < 8) {
            log.debug("No Bearer token in request {} {}", request.method, request.requestURI)
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        if (tokenBlacklistService.isBlacklisted(jwt)) {
            log.warn("Token is blacklisted: {}", jwt.take(15) + "…")
            filterChain.doFilter(request, response)
            return
        }

        val userId = jwtService.extractUserId(jwt)
        if (userId == null) {
            log.warn("Cannot extract userId from token: {}", jwt.take(15) + "…")
            filterChain.doFilter(request, response)
            return
        }

        val user = userRepository.findById(userId)
        if (user == null) {
            log.warn("User {} not found in DB, rejecting request", userId)
            filterChain.doFilter(request, response)
            return
        }

        if (!jwtService.isTokenValid(jwt, userId)) {
            log.warn("Token failed signature/expiry check for user {}", userId)
            filterChain.doFilter(request, response)
            return
        }

        log.info("Authenticated request as user {}", userId)
        val jwtObj = jwtService.parseAsJwt(jwt)
        val auth = JwtAuthenticationToken(jwtObj, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}
