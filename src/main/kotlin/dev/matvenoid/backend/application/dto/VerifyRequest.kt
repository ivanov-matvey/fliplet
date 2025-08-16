package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.NotBlank

data class VerifyRequest (
    @field:NotBlank(message = "Электронная почта не заполнена")
    val email: String,
    val code: String,
)