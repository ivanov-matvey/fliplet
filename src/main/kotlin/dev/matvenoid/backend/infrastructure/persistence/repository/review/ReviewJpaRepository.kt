package dev.matvenoid.backend.infrastructure.persistence.repository.review

import dev.matvenoid.backend.infrastructure.persistence.entity.ReviewJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReviewJpaRepository : JpaRepository<ReviewJpaEntity, UUID>
