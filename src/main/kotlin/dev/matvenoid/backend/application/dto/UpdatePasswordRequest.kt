package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePasswordRequest(
    @field:NotBlank(message = "Пароль не заполнен")
    @field:Size(
        min = 8, max = 64,
        message = "Пароль должен быть от 8 до 64 символов",
    )
    val password: String,

    @field:NotBlank(message = "Новый пароль не заполнен")
    @field:Size(
        min = 8, max = 64,
        message = "Пароль должен быть от 8 до 64 символов",
    )
    val newPassword: String,
)
