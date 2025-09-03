package dev.matvenoid.backend.infrastructure.persistence.repository.cardCollection

import dev.matvenoid.backend.domain.model.CardCollection
import dev.matvenoid.backend.domain.repository.CardCollectionRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CardCollectionRepositoryImpl(
    private val cardCollectionJpaRepository: CardCollectionJpaRepository
) : CardCollectionRepository {
    override fun findAllByUserId(userId: UUID, pageable: Pageable): Page<CardCollection> {
        val cardCollectionEntities = cardCollectionJpaRepository.findAllByUserId(userId, pageable)
        return cardCollectionEntities.map { it.toDomain() }
    }
}