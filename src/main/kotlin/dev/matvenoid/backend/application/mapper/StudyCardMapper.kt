package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.study.StudyCardResponse
import dev.matvenoid.backend.domain.model.Card
import org.springframework.stereotype.Component

@Component
class StudyCardMapper {
    fun toResponse(card: Card): StudyCardResponse =
        StudyCardResponse(
            id = card.id,
            front = card.front,
            back = card.back,
        )
}
