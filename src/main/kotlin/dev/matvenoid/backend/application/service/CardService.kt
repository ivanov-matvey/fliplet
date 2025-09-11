package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.card.CardRequest
import dev.matvenoid.backend.application.dto.card.CardResponse
import dev.matvenoid.backend.application.dto.card.PatchCardRequest
import dev.matvenoid.backend.application.mapper.CardMapper
import dev.matvenoid.backend.application.mapper.PageMapper
import dev.matvenoid.backend.application.usecase.CardUseCase
import dev.matvenoid.backend.domain.exception.AccessDeniedException
import dev.matvenoid.backend.domain.exception.CardCollectionNotFoundException
import dev.matvenoid.backend.domain.model.Card
import dev.matvenoid.backend.domain.repository.CardCollectionRepository
import dev.matvenoid.backend.domain.repository.CardRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val cardCollectionRepository: CardCollectionRepository,
    private val pageMapper: PageMapper,
    private val cardMapper: CardMapper,
) : CardUseCase {
    private val logger = LoggerFactory.getLogger(CardService::class.java)

    @Transactional(readOnly = true)
    override fun getOwnCards(
        userId: UUID,
        pageable: Pageable
    ): PageResponse<CardResponse> {
        val page = cardRepository.findAllByCardCollectionUserId(userId, pageable)
            .map { card ->
                CardResponse(
                    id = card.id,
                    front = card.front,
                    back = card.back,
                )
            }

        return pageMapper.toResponse(page)
    }

    @Transactional(readOnly = true)
    override fun getCardsByCardCollectionId(
        userId: UUID,
        cardCollectionId: UUID,
        pageable: Pageable,
    ): PageResponse<CardResponse> {
        val cardCollection = cardCollectionRepository.findById(cardCollectionId) ?: run {
            logger.warn("Operation Failed: Card collection not found ({})", cardCollectionId)
            throw CardCollectionNotFoundException("Коллекция не найдена")
        }

        if (!cardCollection.isPublic && cardCollection.userId != userId) {
            logger.warn("Access denied to collection {}", cardCollectionId)
            throw AccessDeniedException("Недостаточно прав")
        }

        val page = cardRepository.findAllByCardCollectionId(cardCollectionId, pageable)
            .map { card ->
                CardResponse(
                    id = card.id,
                    front = card.front,
                    back = card.back,
                )
            }

        return pageMapper.toResponse(page)
    }

    @Transactional(readOnly = true)
    override fun getCard(
        id: UUID,
        userId: UUID
    ): CardResponse {
        val card = cardRepository.findById(id) ?: run {
            logger.warn("Operation Failed: Card not found ({})", id)
            throw CardCollectionNotFoundException("Карточка не найдена")
        }

        val cardCollection = cardCollectionRepository.findById(card.cardCollectionId)

        if (!cardCollection!!.isPublic && card.userId != userId) {
            logger.warn("Operation Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        return cardMapper.toResponse(card)
    }

    @Transactional
    override fun createCard(
        userId: UUID,
        cardCollectionId: UUID,
        request: CardRequest
    ): CardResponse {
        val cardCollection = cardCollectionRepository.findById(cardCollectionId) ?: run {
            logger.warn("Create Failed: Card collection not found ({})", cardCollectionId)
            throw CardCollectionNotFoundException("Коллекция не найдена")
        }

        if (cardCollection.userId != userId) {
            logger.warn("Create Failed: Insufficient permissions ({})", cardCollectionId)
            throw AccessDeniedException("Недостаточно прав")
        }

        val card = Card.create(
            cardCollectionId = cardCollectionId,
            userId = userId,
            front = request.front,
            back = request.back,
        )

        val savedCard = cardRepository.save(card)

        logger.info("Card created ({})", card.id)
        return cardMapper.toResponse(savedCard)
    }

    @Transactional
    override fun patchCard(
        id: UUID,
        userId: UUID,
        request: PatchCardRequest
    ): CardResponse {
        val card = cardRepository.findById(id) ?: run {
            logger.warn("Update Failed: Card not found ({})", id)
            throw CardCollectionNotFoundException("Карточка не найдена")
        }

        if (card.userId != userId) {
            logger.warn("Update Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        val updateCard = cardRepository.save(
            card.copy(
                front = request.front ?: card.front,
                back = request.back ?: card.back,
                updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )

        logger.info("Card updated ({})", id)
        return cardMapper.toResponse(updateCard)
    }

    @Transactional
    override fun deleteCard(id: UUID, userId: UUID) {
        val card = cardRepository.findById(id) ?: run {
            logger.warn("Delete Failed: Card not found ({})", id)
            throw CardCollectionNotFoundException("Карточка не найдена")
        }

        if (card.userId != userId) {
            logger.warn("Delete Failed: Insufficient permissions ({})", id)
            throw AccessDeniedException("Недостаточно прав")
        }

        cardRepository.delete(card)
        logger.info("Card deleted ({})", id)
    }
}
