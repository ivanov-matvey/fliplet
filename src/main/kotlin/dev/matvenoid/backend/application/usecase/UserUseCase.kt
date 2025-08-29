package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.UserResponse
import java.util.UUID

interface UserUseCase {
    fun findById(id: UUID): UserResponse
    fun findByUsername(username: String): UserResponse
    fun updateEmail(id: UUID, newEmail: String)
    fun updateName(id: UUID, newName: String): UserResponse
    fun updateUsername(id: UUID, newUsername: String): UserResponse
}