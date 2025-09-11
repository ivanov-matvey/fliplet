package dev.matvenoid.backend.application.dto.user

import jakarta.validation.constraints.NotBlank

data class PatchNameRequest(
    @field:NotBlank(message = "Имя не заполнено")
    val name: String,
)
