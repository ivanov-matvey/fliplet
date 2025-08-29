package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.UpdateEmailRequest
import dev.matvenoid.backend.application.dto.UpdateNameRequest
import dev.matvenoid.backend.application.dto.UpdatePasswordRequest
import dev.matvenoid.backend.application.dto.UpdateUsernameRequest
import dev.matvenoid.backend.application.dto.UserResponse
import java.util.UUID

interface UserUseCase {
    fun findById(id: UUID): UserResponse
    fun findByUsername(username: String): UserResponse
    fun updateEmail(id: UUID, request: UpdateEmailRequest)
    fun updateName(id: UUID, request: UpdateNameRequest): UserResponse
    fun updateUsername(id: UUID, request: UpdateUsernameRequest): UserResponse
    fun updatePassword(id: UUID, request: UpdatePasswordRequest): UserResponse
}