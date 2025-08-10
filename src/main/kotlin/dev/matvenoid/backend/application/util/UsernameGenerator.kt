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

    private val slugRegex   = Regex("[^a-z0-9_-]")
    private val dashCleanup = Regex("-+")

    fun generate(baseName: String): String {
        val rawBase = baseName
            .lowercase()
            .replace(" ", "-")
            .replace(slugRegex, "")
            .replace(dashCleanup, "-")
            .trim('-')
            .ifEmpty { "user" }

        val base = when {
            rawBase.length < 3 -> "user"
            rawBase in reserved -> "user-$rawBase"
            else -> rawBase
        }

        var suffix   = 0
        var candidate: String
        do {
            candidate = if (suffix == 0) base else "$base$suffix"
            suffix++
        } while (
            candidate in reserved ||
            userRepository.existsByUsernameCi(candidate.lowercase())
        )

        return candidate
    }
}
