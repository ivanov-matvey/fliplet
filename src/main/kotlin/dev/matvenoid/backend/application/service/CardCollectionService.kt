package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.cardCollection.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.cardCollection.CardCollectionRequest
import dev.matvenoid.backend.application.dto.cardCollection.PatchCardCollectionRequest
import dev.matvenoid.backend.application.mapper.CardCollectionMapper
import dev.matvenoid.backend.application.mapper.PageMapper
import dev.matvenoid.backend.application.usecase.CardCollectionUseCase
import dev.matvenoid.backend.domain.exception.AccessDeniedException
import dev.matvenoid.backend.domain.exception.CardCollectionNotFoundException
import dev.matvenoid.backend.domain.model.CardCollection
import dev.matvenoid.backend.domain.repository.CardCollectionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class CardCollectionService(
    private val cardCollectionRepository: CardCollectionRepository,
    private val pageMapper: PageMapper,
    private val cardCollectionMapper: CardCollectionMapper
) : CardCollectionUseCase {
    private val logger = LoggerFactory.getLogger(CardCollectionService::class.java)

    @Transactional(readOnly = true)
    override fun getOwnCardCollections(
        userId: UUID,
        pageable: Pageable,
    ): PageResponse<CardCollectionResponse> {
        val page = cardCollectionRepository.findAllByUserId(userId, pageable)
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

    @Transactional(readOnly = true)
    override fun getCardCollectionsByUserId(
        userId: UUID,
        pageable: Pageable,
    ): PageResponse<CardCollectionResponse> {
        val page = cardCollectionRepository.findAllByUserIdAndIsPublicTrue(userId, pageable)
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

    @Transactional(readOnly = true)
    override fun getCardCollection(
        id: UUID,
        userId: UUID
    ): CardCollectionResponse {
        val cardCollection = cardCollectionRepository.findById(id) ?: run {
            logger.warn("Operation Failed: Card collection not found ({})", id)
            throw CardCollectionNotFoundException("Коллекция не найдена")
        }

        if (!cardCollection.isPublic && cardCollection.userId != userId) {
            logger.warn("Operation Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        return cardCollectionMapper.toResponse(cardCollection)
    }

    @Transactional
    override fun createCardCollection(
        userId: UUID,
        request: CardCollectionRequest
    ): CardCollectionResponse {
        val cardCollection = CardCollection.create(
            userId = userId,
            name = request.name,
            description = request.description,
            isPublic = request.isPublic ?: true,
        )

        val savedCardCollection = cardCollectionRepository.save(cardCollection)

        logger.info("Card collection created ({})", cardCollection.id)
        return cardCollectionMapper.toResponse(savedCardCollection)
    }

    @Transactional
    override fun patchCardCollection(
        id: UUID,
        userId: UUID,
        request: PatchCardCollectionRequest
    ): CardCollectionResponse {
        val cardCollection = cardCollectionRepository.findById(id) ?: run {
            logger.warn("Update Failed: Card collection not found ({})", id)
            throw CardCollectionNotFoundException("Коллекция не найдена")
        }

        if (cardCollection.userId != userId) {
            logger.warn("Update Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        val updateCardCollection = cardCollectionRepository.save(
            cardCollection.copy(
                name = request.name ?: cardCollection.name,
                description = request.description ?: cardCollection.description,
                isPublic = request.isPublic ?: cardCollection.isPublic,
                updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )

        logger.info("Card collection updated ({})", id)
        return cardCollectionMapper.toResponse(updateCardCollection)
    }

    @Transactional
    override fun deleteCardCollection(id: UUID, userId: UUID,) {
        val cardCollection = cardCollectionRepository.findById(id) ?: run {
            logger.warn("Delete Failed: Card collection not found ({})", id)
            throw CardCollectionNotFoundException("Коллекция не найдена")
        }

        if (cardCollection.userId != userId) {
            logger.warn("Delete Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        cardCollectionRepository.delete(cardCollection)
        logger.info("Card collection deleted ({})", id)
    }
}