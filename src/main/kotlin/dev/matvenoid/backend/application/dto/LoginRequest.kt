package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Адрес электронной почты не заполнен")
    @field:Email(message = "Некорректный адрес электронной почты")
    val email: String,

    @field:NotBlank(message = "Пароль не заполнен")
    @field:Size(
        min = 8, max = 64,
        message = "Пароль должен быть от 8 до 64 символов"
    )
    val password: String,
)
