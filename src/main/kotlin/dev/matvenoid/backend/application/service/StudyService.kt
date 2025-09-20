package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.study.ReviewRequest
import dev.matvenoid.backend.application.dto.study.ReviewResponse
import dev.matvenoid.backend.application.dto.study.StudyCardResponse
import dev.matvenoid.backend.application.mapper.ReviewMapper
import dev.matvenoid.backend.application.mapper.StudyCardMapper
import dev.matvenoid.backend.application.usecase.StudyUseCase
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.model.Review
import dev.matvenoid.backend.domain.repository.CardProgressRepository
import dev.matvenoid.backend.domain.repository.CardRepository
import dev.matvenoid.backend.domain.repository.ReviewRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class StudyService(
    private val cardRepository: CardRepository,
    private val cardProgressRepository: CardProgressRepository,
    private val reviewRepository: ReviewRepository,
    private val studyCardMapper: StudyCardMapper,
    private val reviewMapper: ReviewMapper,
) : StudyUseCase {
    private val logger = LoggerFactory.getLogger(StudyService::class.java)

    @Transactional(readOnly = true)
    override fun getNextCard(userId: UUID): StudyCardResponse? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val progress = cardProgressRepository.findFirstDueByUserId(userId, now) ?: return null

        val card = cardRepository.findById(progress.cardId) ?: return null

        return studyCardMapper.toResponse(card)
    }

    @Transactional
    override fun reviewCard(userId: UUID, request: ReviewRequest): ReviewResponse {
        val progress = cardProgressRepository.findByCardIdAndUserId(request.cardId, userId) ?: run {
            logger.warn("Review Failed: Card progress not found ({})", request.cardId)
            throw UserNotFoundException("Прогресс по карточке не найден")
        }

        val oldIntervalDays = progress.intervalDays
        val oldEaseFactor = progress.easeFactor
        val oldRepetition = progress.repetition

        val updated = progress.review(request.quality)
        val savedProgress = cardProgressRepository.save(updated)

        val review = Review.create(
            cardId = request.cardId,
            userId = userId,
            quality = request.quality,
            prevIntervalDays = oldIntervalDays,
            newIntervalDays = savedProgress.intervalDays,
            prevEaseFactor = oldEaseFactor,
            newEaseFactor = savedProgress.easeFactor,
            prevRepetitions = oldRepetition,
            newRepetitions = savedProgress.repetition,
        )
        reviewRepository.save(review)

        logger.info("Card reviewed: ({}), ({})", request.cardId, request.quality)

        return reviewMapper.toResponse(review, savedProgress)
    }
}
