package dev.matvenoid.backend.application.dto.card

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CardRequest(
    @field:NotNull(message = "Коллекция не указана")
    val cardCollectionId: UUID,

    @field:NotBlank(message = "Вопрос не заполнен")
    val front: String,

    @field:NotBlank(message = "Ответ не заполнен")
    val back: String,
)
