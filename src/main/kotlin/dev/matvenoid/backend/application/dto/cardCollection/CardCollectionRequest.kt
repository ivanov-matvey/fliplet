package dev.matvenoid.backend.application.dto.cardCollection

import jakarta.validation.constraints.NotBlank

data class CardCollectionRequest(
    @field:NotBlank(message = "Имя коллекции не заполнено")
    val name: String,

    val description: String?,

    val isPublic: Boolean?,
)
