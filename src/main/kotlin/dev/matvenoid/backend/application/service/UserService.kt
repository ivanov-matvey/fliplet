package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.usecase.UserUseCase
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) : UserUseCase {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    override fun findById(id: UUID): UserResponse? {
        logger.info("Fetching user by ID: {}", id)

        val user = userRepository.findById(id) ?: run {
            logger.warn("User not found by ID: {}", id)
            throw UserNotFoundException("Пользователь не найден")
        }

        logger.info("User {} found, preparing response", id)
        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
        )
    }

    override fun findByUsername(username: String): UserResponse? {
        logger.info("Fetching user by username: {}", username)

        val user = userRepository.findByUsername(username) ?: run {
            logger.warn("User not found by username: {}", username)
            throw UserNotFoundException("Пользователь $username не найден")
        }

        logger.info("User {} found, preparing response", username)
        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
        )
    }
}
