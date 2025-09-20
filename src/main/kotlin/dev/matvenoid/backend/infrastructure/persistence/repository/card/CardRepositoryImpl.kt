package dev.matvenoid.backend.infrastructure.persistence.repository.card

import dev.matvenoid.backend.domain.model.Card
import dev.matvenoid.backend.domain.repository.CardRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.repository.card_collection.CardCollectionJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CardRepositoryImpl(
    private val cardJpaRepository: CardJpaRepository,
    private val cardCollectionJpaRepository: CardCollectionJpaRepository,
) : CardRepository {
    override fun findById(id: UUID): Card? {
        val cardEntity = cardJpaRepository.findById(id)
        return cardEntity.orElse(null)?.toDomain()
    }

    override fun findAllByCardCollectionUserId(userId: UUID, pageable: Pageable): Page<Card> {
        val cardEntities = cardJpaRepository.findAllByCardCollectionUserId(userId, pageable)
        return cardEntities.map { it.toDomain() }
    }

    override fun findAllByCardCollectionId(cardCollectionId: UUID, pageable: Pageable): Page<Card> {
        val cardEntities = cardJpaRepository.findAllByCardCollectionId(cardCollectionId, pageable)
        return cardEntities.map { it.toDomain() }
    }

    override fun save(card: Card): Card {
        val cardCollectionRef = cardCollectionJpaRepository.getReferenceById(card.cardCollectionId)
        val cardEntity = card.toJpaEntity(cardCollectionRef)
        val saved = cardJpaRepository.save(cardEntity)
        return saved.toDomain()
    }

    override fun saveAll(cards: List<Card>): List<Card> {
        val cardCollectionRefs = cards
            .map { it.cardCollectionId }
            .distinct()
            .associateWith { cardCollectionJpaRepository.getReferenceById(it) }

        val cardEntities = cards.map { it.toJpaEntity(cardCollectionRefs[it.cardCollectionId]!!) }
        val saved = cardJpaRepository.saveAll(cardEntities)
        return saved.map { it.toDomain() }
    }

    override fun delete(card: Card) {
        val cardCollectionRef = cardCollectionJpaRepository.getReferenceById(card.cardCollectionId)
        val cardEntity = card.toJpaEntity(cardCollectionRef)
        cardJpaRepository.delete(cardEntity)
    }
}
