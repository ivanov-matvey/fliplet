package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.card_collection.CardCollectionResponse
import dev.matvenoid.backend.domain.model.CardCollection
import org.springframework.stereotype.Component

@Component
class CardCollectionMapper {
    fun toResponse(cardCollection: CardCollection): CardCollectionResponse =
        CardCollectionResponse(
            id = cardCollection.id,
            name = cardCollection.name,
            description = cardCollection.description,
            public = cardCollection.isPublic,
        )
}
