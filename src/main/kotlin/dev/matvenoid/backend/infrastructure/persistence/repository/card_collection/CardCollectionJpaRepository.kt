package dev.matvenoid.backend.infrastructure.persistence.repository.card_collection

import dev.matvenoid.backend.infrastructure.persistence.entity.CardCollectionJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CardCollectionJpaRepository : JpaRepository<CardCollectionJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<CardCollectionJpaEntity>
    fun findAllByUserIdAndIsPublicTrue(userId: UUID, pageable: Pageable): Page<CardCollectionJpaEntity>
}
