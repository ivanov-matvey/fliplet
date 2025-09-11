package dev.matvenoid.backend.application.dto.user

import jakarta.validation.constraints.NotBlank

data class AvatarConfirmRequest(
    @field:NotBlank(message = "Ключ не заполнен")
    val key: String
)
