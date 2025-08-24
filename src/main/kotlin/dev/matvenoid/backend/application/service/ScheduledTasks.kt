package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class ScheduledTasks(
    private val userRepository: UserRepository
) {
    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    fun deleteUnverifiedUsers() {
        val cutoff = OffsetDateTime.now(ZoneOffset.UTC).minusHours(48)
        val usersToDelete = userRepository.findAllUnverifiedCreatedBefore(cutoff)
        usersToDelete.forEach { user ->
            userRepository.delete(user)
        }
    }
}
