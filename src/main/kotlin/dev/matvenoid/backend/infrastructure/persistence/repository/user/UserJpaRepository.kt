package dev.matvenoid.backend.infrastructure.persistence.repository.user

import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): UserJpaEntity?
    fun existsByUsernameCi(usernameCi: String): Boolean
    fun findByUsernameCi(usernameCi: String): UserJpaEntity?
}
