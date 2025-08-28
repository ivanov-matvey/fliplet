package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.User
import java.time.OffsetDateTime
import java.util.UUID

interface UserRepository {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
    fun findByPendingEmail(email: String): User?
    fun findById(id: UUID): User?
    fun save(user: User): User
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
    fun findAllUnverifiedCreatedBefore(cutoff: OffsetDateTime): List<User>
    fun delete(user: User)
}
