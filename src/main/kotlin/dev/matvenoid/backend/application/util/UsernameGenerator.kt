package dev.matvenoid.backend.application.util

import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UsernameGenerator(
    private val userRepository: UserRepository
) {

    private val reserved = setOf(
        "admin", "administrator", "root",
        "me", "my", "self",
        "system", "support", "help", "api"
    )

    private val slugRegex = Regex("[^a-z0-9_-]")
    private val dashCleanup = Regex("-+")


    fun generate(email: String): String {
        val localPart = email.substringBefore('@')
        val slugBase = localPart
            .lowercase()
            .replace(" ", "-")
            .replace(slugRegex, "")
            .replace(dashCleanup, "-")
            .trim('-')
            .let {
                when {
                    it.length < 3 -> "user"
                    it in reserved -> "user-$it"
                    else -> it
                }
            }

        var candidate = slugBase.take(32)
        var suffix = 0

        while (candidate in reserved || userRepository.existsByUsername(candidate)) {
            suffix += 1
            val tail = suffix.toString()
            val maxBaseLen = 32 - tail.length
            candidate = slugBase.take(maxBaseLen) + tail
        }

        return candidate
    }
}
