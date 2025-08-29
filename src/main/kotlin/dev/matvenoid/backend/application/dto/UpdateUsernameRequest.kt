package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateUsernameRequest(
    @field:NotBlank(message = "Имя пользователя не заполнено")
    @field:Pattern(
        regexp = "^[a-z0-9_-]{3,32}$",
        message = "Можно использовать латинские буквы, цифры, «_» и «-». Длина - от 3 до 32 символов."
    )
    val username: String,
)
