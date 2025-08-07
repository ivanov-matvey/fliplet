package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.User
import java.util.UUID

interface UserRepository {
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): User?
    fun findById(id: UUID): User?
    fun save(user: User): User
}
