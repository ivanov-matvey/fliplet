package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.user.PatchEmailRequest
import dev.matvenoid.backend.application.dto.user.PatchNameRequest
import dev.matvenoid.backend.application.dto.user.PatchPasswordRequest
import dev.matvenoid.backend.application.dto.user.PatchUsernameRequest
import dev.matvenoid.backend.application.dto.user.UserPublicResponse
import dev.matvenoid.backend.application.dto.user.UserResponse
import java.util.UUID

interface UserUseCase {
    fun findById(id: UUID): UserResponse
    fun findByUsername(username: String): UserPublicResponse
    fun patchEmail(id: UUID, request: PatchEmailRequest)
    fun patchName(id: UUID, request: PatchNameRequest): UserResponse
    fun patchUsername(id: UUID, request: PatchUsernameRequest): UserResponse
    fun patchPassword(id: UUID, request: PatchPasswordRequest): UserResponse
    fun patchAvatarUrl(id: UUID, key: String): UserResponse
}