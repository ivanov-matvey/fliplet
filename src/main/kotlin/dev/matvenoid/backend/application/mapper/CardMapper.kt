package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.card.CardResponse
import dev.matvenoid.backend.domain.model.Card
import org.springframework.stereotype.Component

@Component
class CardMapper {
    fun toResponse(card: Card): CardResponse =
        CardResponse(
            id = card.id,
            front = card.front,
            back = card.back,
        )
}
