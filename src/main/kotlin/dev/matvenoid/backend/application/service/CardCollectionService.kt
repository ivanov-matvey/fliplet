package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.mapper.PageMapper
import dev.matvenoid.backend.application.usecase.CardCollectionUseCase
import dev.matvenoid.backend.domain.repository.CardCollectionRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CardCollectionService(
    private val cardCollectionRepository: CardCollectionRepository,
    private val pageMapper: PageMapper
) : CardCollectionUseCase {
    override fun getUserCardCollections(
        id: UUID,
        pageable: Pageable,
    ): PageResponse<CardCollectionResponse> {
        val page = cardCollectionRepository.findAllByUserId(id, pageable)
            .map { cardCollection ->
                CardCollectionResponse(
                    id = cardCollection.id,
                    name = cardCollection.name,
                    description = cardCollection.description,
                    isPublic = cardCollection.isPublic,
                )
            }

        return pageMapper.toResponse(page)
    }
}