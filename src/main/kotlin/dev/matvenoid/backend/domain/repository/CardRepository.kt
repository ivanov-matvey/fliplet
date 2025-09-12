package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.Card
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardRepository {
    fun findById(id: UUID): Card?
    fun findAllByCardCollectionUserId(userId: UUID, pageable: Pageable): Page<Card>
    fun findAllByCardCollectionId(cardCollectionId: UUID, pageable: Pageable): Page<Card>
    fun save(card: Card): Card
    fun saveAll(cards: List<Card>): List<Card>
    fun delete(card: Card)
}
