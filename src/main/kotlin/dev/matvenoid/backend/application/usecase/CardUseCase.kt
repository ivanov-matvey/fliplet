package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.card.CardRequest
import dev.matvenoid.backend.application.dto.card.CardResponse
import dev.matvenoid.backend.application.dto.card.PatchCardRequest
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardUseCase {
    fun getOwnCards(userId: UUID, pageable: Pageable): PageResponse<CardResponse>
    fun getCardsByCardCollectionId(userId: UUID, cardCollectionId: UUID, pageable: Pageable): PageResponse<CardResponse>
    fun getCard(id: UUID, userId: UUID): CardResponse
    fun createCard(userId: UUID, cardCollectionId: UUID, request: CardRequest): CardResponse
    fun patchCard(id: UUID, userId: UUID, request: PatchCardRequest): CardResponse
    fun deleteCard(id: UUID, userId: UUID)
}
