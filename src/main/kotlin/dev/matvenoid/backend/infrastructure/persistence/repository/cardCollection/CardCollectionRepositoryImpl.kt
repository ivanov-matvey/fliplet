package dev.matvenoid.backend.infrastructure.persistence.repository.cardCollection

import dev.matvenoid.backend.domain.model.CardCollection
import dev.matvenoid.backend.domain.repository.CardCollectionRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.repository.user.UserJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CardCollectionRepositoryImpl(
    private val cardCollectionJpaRepository: CardCollectionJpaRepository,
    private val userJpaRepository: UserJpaRepository
) : CardCollectionRepository {
    override fun findById(id: UUID): CardCollection? {
        val cardCollectionEntity = cardCollectionJpaRepository.findById(id)
        return cardCollectionEntity.orElse(null)?.toDomain()
    }

    override fun findAllByUserId(userId: UUID, pageable: Pageable): Page<CardCollection> {
        val cardCollectionEntities = cardCollectionJpaRepository.findAllByUserId(userId, pageable)
        return cardCollectionEntities.map { it.toDomain() }
    }

    override fun findAllByUserIdAndIsPublicTrue(userId: UUID, pageable: Pageable): Page<CardCollection> {
        val cardCollectionEntities = cardCollectionJpaRepository.findAllByUserIdAndIsPublicTrue(userId, pageable)
        return cardCollectionEntities.map { it.toDomain() }
    }

    override fun save(cardCollection: CardCollection): CardCollection {
        val userRef = userJpaRepository.getReferenceById(cardCollection.userId)
        val cardCollectionEntity = cardCollection.toJpaEntity(userRef)
        val saved = cardCollectionJpaRepository.save(cardCollectionEntity)
        return saved.toDomain()
    }

    override fun delete(cardCollection: CardCollection) {
        val userRef = userJpaRepository.getReferenceById(cardCollection.userId)
        val cardCollectionEntity = cardCollection.toJpaEntity(userRef)
        cardCollectionJpaRepository.delete(cardCollectionEntity)
    }
}