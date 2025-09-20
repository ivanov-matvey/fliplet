package dev.matvenoid.backend.application.dto.card_collection

import jakarta.validation.constraints.NotBlank

data class CardCollectionRequest(
    @field:NotBlank(message = "Имя коллекции не заполнено")
    val name: String,

    val description: String?,

    val public: Boolean?,
)
