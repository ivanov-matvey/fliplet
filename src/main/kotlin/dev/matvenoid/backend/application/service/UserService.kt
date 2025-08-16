package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.usecase.UserUseCase
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) : UserUseCase {
    override fun findById(id: UUID): UserResponse? {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("Пользователь не найден")

        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
        )
    }

    override fun findByUsername(username: String): UserResponse? {
        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException("Пользователь $username не найден")

        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
            )
    }
}
