package dev.matvenoid.backend.infrastructure.persistence.repository.user

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun existsByPhone(phone: String): Boolean {
        return userJpaRepository.existsByPhone(phone)
    }

    override fun findByPhone(phone: String): User? {
        val userEntity = userJpaRepository.findByPhone(phone)
        return userEntity?.toDomain()
    }

    override fun save(user: User): User {
        val entity = user.toJpaEntity()
        val saved = userJpaRepository.save(entity)
        return saved.toDomain()
    }
}
