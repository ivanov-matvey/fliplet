package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.CardCollection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CardCollectionRepository {
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<CardCollection>
}