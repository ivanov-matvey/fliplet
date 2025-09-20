package dev.matvenoid.backend.infrastructure.persistence.repository.card_progress

import dev.matvenoid.backend.domain.model.CardProgress
import dev.matvenoid.backend.domain.repository.CardProgressRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.repository.card.CardJpaRepository
import dev.matvenoid.backend.infrastructure.persistence.repository.user.UserJpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class CardProgressRepositoryImpl(
    private val cardProgressJpaRepository: CardProgressJpaRepository,
    private val cardJpaRepository: CardJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : CardProgressRepository {

    override fun findByCardIdAndUserId(cardId: UUID, userId: UUID): CardProgress? {
        val entity = cardProgressJpaRepository.findByCardIdAndUserId(cardId, userId)
        return entity?.toDomain()
    }

    override fun findFirstDueByUserId(userId: UUID, now: OffsetDateTime): CardProgress? {
        val entities = cardProgressJpaRepository.findAllByUserIdAndNextReviewAtBeforeOrderByNextReviewAtAsc(userId, now)
        return entities.firstOrNull()?.toDomain()
    }

    override fun save(cardProgress: CardProgress): CardProgress {
        val cardRef = cardJpaRepository.getReferenceById(cardProgress.cardId)
        val userRef = userJpaRepository.getReferenceById(cardProgress.userId)
        val entity = cardProgress.toJpaEntity(cardRef, userRef)
        val saved = cardProgressJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun saveAll(cardProgresses: List<CardProgress>): List<CardProgress> {
        val cardRefs = cardProgresses
            .map { it.cardId }
            .distinct()
            .associateWith { cardJpaRepository.getReferenceById(it) }

        val userRefs = cardProgresses
            .map { it.userId }
            .distinct()
            .associateWith { userJpaRepository.getReferenceById(it) }

        val cardProgressEntities = cardProgresses.map { it.toJpaEntity(cardRefs[it.cardId]!!, userRefs[it.userId]!!) }
        val saved = cardProgressJpaRepository.saveAll(cardProgressEntities)
        return saved.map { it.toDomain() }
    }
}
