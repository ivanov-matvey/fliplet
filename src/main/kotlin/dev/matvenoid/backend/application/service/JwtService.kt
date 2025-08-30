package dev.matvenoid.backend.application.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @param:Value($$"${jwt.secret}") private val secretKey: String,
    @param:Value($$"${jwt.access-token-expiration-ms}") private val accessTokenExpiration: Long,
    @param:Value($$"${jwt.refresh-token-expiration-ms}") private val refreshTokenExpiration: Long
) {
    private val logger = LoggerFactory.getLogger(JwtService::class.java)

    private val signInKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userId: UUID, expiration: Long): String {
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(signInKey)
            .compact()
    }

    fun generateAccessToken(userId: UUID): String {
        logger.info("Generate new access token ({})", userId)
        return generateToken(userId, accessTokenExpiration)
    }

    fun generateRefreshToken(userId: UUID): String {
        logger.info("Generate new refresh token ({})", userId)
        return generateToken(userId, refreshTokenExpiration)
    }

    fun isTokenValid(token: String, userId: UUID): Boolean {
        try {
            val claims = extractAllClaims(token)
            val notExpired = claims.expiration.after(Date())
            val subjectOk  = UUID.fromString(claims.subject) == userId
            val valid = notExpired && subjectOk
            return valid
        } catch (e: JwtException) {
            logger.warn("JWT validation failed: {}", e.message)
            return false
        }
    }

    fun extractUserId(token: String): UUID? {
        return try {
            val id = UUID.fromString(extractClaim(token, Claims::getSubject))
            id
        } catch (e: Exception) {
            logger.warn("Extract user ID from token failed: {}", e.message)
            null
        }
    }

    private fun extractExpiration(token: String): Date? =
        extractClaim(token, Claims::getExpiration)

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signInKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun getRemainingExpiration(token: String): Duration {
        val expirationDate = extractExpiration(token)
            ?: throw JwtException("Не удалось извлечь дату истечения срока действия из токена")
        val remainingMs = expirationDate.time - System.currentTimeMillis()
        return Duration.ofMillis(if (remainingMs > 0) remainingMs else 0)
    }

    fun parseAsJwt(token: String): Jwt {
        val claims = extractAllClaims(token)
        return Jwt(
            token,
            Instant.ofEpochMilli(claims.issuedAt.time),
            Instant.ofEpochMilli(claims.expiration.time),
            mapOf("alg" to "HS512"),
            claims
        )
    }
}
