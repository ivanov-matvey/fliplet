package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.security.UserPrincipal
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value($$"${jwt.secret}")
    private lateinit var secretKey: String

    @Value($$"${jwt.access-token-expiration-ms}")
    private lateinit var accessTokenExpiration: String

    @Value($$"${jwt.refresh-token-expiration-ms}")
    private lateinit var refreshTokenExpiration: String

    fun generateToken(userDetails: UserPrincipal, expiration: Long): String {
        return Jwts.builder()
            .subject(userDetails.id.toString())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey())
            .compact()
    }

    fun generateAccessToken(userDetails: UserPrincipal): String {
        return generateToken(userDetails, accessTokenExpiration.toLong())
    }

    fun generateRefreshToken(userDetails: UserPrincipal): String {
        return generateToken(userDetails, refreshTokenExpiration.toLong())
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userIdFromToken = extractUserId(token)

        return if (userDetails is UserPrincipal && userIdFromToken != null) {
            (userIdFromToken == userDetails.id) && !isTokenExpired(token)
        } else {
            false
        }
    }

    fun extractUserId(token: String): UUID? {
        val idString = extractClaim(token, Claims::getSubject)
        return try {
            UUID.fromString(idString)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token)?.before(Date()) ?: true
    }

    private fun extractExpiration(token: String): Date? {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getRemainingExpiration(token: String): Duration {
        val expirationDate = extractExpiration(token)
            ?: throw JwtException("Не удалось извлечь дату истечения срока действия из токена")
        val remainingMs = expirationDate.time - System.currentTimeMillis()
        return Duration.ofMillis(if (remainingMs > 0) remainingMs else 0)
    }
}
