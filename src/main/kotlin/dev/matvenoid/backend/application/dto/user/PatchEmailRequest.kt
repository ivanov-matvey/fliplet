package dev.matvenoid.backend.application.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PatchEmailRequest (
    @field:NotBlank(message = "Адрес электронной почты не заполнен")
    @field:Email(message = "Некорректный адрес электронной почты")
    val email: String,
)
