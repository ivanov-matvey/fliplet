package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.cardCollection.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.cardCollection.CardCollectionRequest
import dev.matvenoid.backend.application.dto.cardCollection.PatchCardCollectionRequest
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardCollectionUseCase {
    fun getOwnCardCollections(userId: UUID, pageable: Pageable): PageResponse<CardCollectionResponse>
    fun getCardCollectionsByUserId(userId: UUID, pageable: Pageable): PageResponse<CardCollectionResponse>
    fun getCardCollection(id: UUID, userId: UUID): CardCollectionResponse
    fun createCardCollection(userId: UUID, request: CardCollectionRequest): CardCollectionResponse
    fun patchCardCollection(id: UUID, userId: UUID, request: PatchCardCollectionRequest): CardCollectionResponse
    fun deleteCardCollection(id: UUID, userId: UUID)
}