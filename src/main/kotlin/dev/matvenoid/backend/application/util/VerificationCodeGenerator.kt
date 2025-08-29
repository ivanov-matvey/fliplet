package dev.matvenoid.backend.application.util

import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class VerificationCodeGenerator {
    private val random = SecureRandom()

    fun generateCode(): String {
        return (100_000 + random.nextInt(900_000)).toString()
    }
}
