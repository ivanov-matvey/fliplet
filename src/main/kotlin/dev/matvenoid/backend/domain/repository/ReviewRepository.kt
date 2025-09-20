package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.Review

interface ReviewRepository {
    fun save(review: Review): Review
}
