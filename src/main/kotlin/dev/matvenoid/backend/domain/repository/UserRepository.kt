package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.User

interface UserRepository {
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): User?
    fun save(user: User): User
}
