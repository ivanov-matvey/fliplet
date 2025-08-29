package dev.matvenoid.backend.application.dto

import jakarta.validation.constraints.NotBlank

data class UpdateNameRequest(
    @field:NotBlank(message = "Имя не заполнено")
    val name: String,
)
