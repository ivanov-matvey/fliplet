package dev.matvenoid.backend.application.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerifyRequest (
    @field:NotBlank(message = "Адрес электронной почты не заполнен")
    @field:Email(message = "Некорректный адрес электронной почты")
    val email: String,

    @field:NotBlank(message = "Код подтверждения не заполнен")
    @field:Pattern(
        regexp = "\\d{6}",
        message = "Код подтверждения должен содержать ровно 6 цифр"
    )
    val code: String,
)
