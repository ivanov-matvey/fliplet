package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ResendVerificationCodeRequest(
    @field:NotBlank(message = "Адрес электронной почты не заполнен")
    @field:Email(message = "Некорректный адрес электронной почты")
    val email: String,
)
