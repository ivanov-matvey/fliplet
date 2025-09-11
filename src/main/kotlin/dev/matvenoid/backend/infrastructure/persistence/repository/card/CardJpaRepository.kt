package dev.matvenoid.backend.infrastructure.persistence.repository.card

import dev.matvenoid.backend.infrastructure.persistence.entity.CardJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CardJpaRepository : JpaRepository<CardJpaEntity, UUID> {
    fun findAllByCardCollectionUserId(userId: UUID, pageable: Pageable): Page<CardJpaEntity>
    fun findAllByCardCollectionId(cardCollectionId: UUID, pageable: Pageable): Page<CardJpaEntity>
}
