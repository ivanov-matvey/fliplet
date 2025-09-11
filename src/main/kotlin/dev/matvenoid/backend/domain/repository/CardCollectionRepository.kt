package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.CardCollection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardCollectionRepository {
    fun findById(id: UUID): CardCollection?
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<CardCollection>
    fun findAllByUserIdAndIsPublicTrue(userId: UUID, pageable: Pageable): Page<CardCollection>
    fun save(cardCollection: CardCollection): CardCollection
    fun delete(cardCollection: CardCollection)
}