package dev.matvenoid.backend.infrastructure.persistence.repository.user

import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserJpaEntity?
    fun findByPendingEmail(email: String): UserJpaEntity?
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserJpaEntity?
    fun findByIsEmailVerifiedFalseAndCreatedAtBefore(cutoff: OffsetDateTime): List<UserJpaEntity>
}
