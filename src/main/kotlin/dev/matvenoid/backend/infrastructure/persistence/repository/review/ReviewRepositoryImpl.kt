package dev.matvenoid.backend.infrastructure.persistence.repository.review

import dev.matvenoid.backend.domain.model.Review
import dev.matvenoid.backend.domain.repository.ReviewRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.repository.user.UserJpaRepository
import dev.matvenoid.backend.infrastructure.persistence.repository.card.CardJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ReviewRepositoryImpl(
    private val reviewJpaRepository: ReviewJpaRepository,
    private val cardJpaRepository: CardJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : ReviewRepository {

    override fun save(review: Review): Review {
        val cardRef = cardJpaRepository.getReferenceById(review.cardId)
        val userRef = userJpaRepository.getReferenceById(review.userId)
        val entity = review.toJpaEntity(cardRef, userRef)
        val saved = reviewJpaRepository.save(entity)
        return saved.toDomain()
    }
}
