package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardCollectionUseCase {
    fun getUserCardCollections(id: UUID, pageable: Pageable): PageResponse<CardCollectionResponse>
}