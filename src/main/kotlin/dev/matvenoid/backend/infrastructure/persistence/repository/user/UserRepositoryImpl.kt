package dev.matvenoid.backend.infrastructure.persistence.repository.user

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun existsByEmail(email: String): Boolean =
        userJpaRepository.existsByEmail(email)

    override fun findByEmail(email: String): User? {
        val userEntity = userJpaRepository.findByEmail(email)
        return userEntity?.toDomain()
    }

    override fun findByPendingEmail(email: String): User? {
        val userEntity = userJpaRepository.findByPendingEmail(email)
        return userEntity?.toDomain()
    }

    override fun findById(id: UUID): User? {
        val userEntity = userJpaRepository.findById(id)
        return userEntity.orElse(null)?.toDomain()
    }

    override fun save(user: User): User {
        val entity = user.toJpaEntity()
        val saved = userJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun existsByUsername(username: String) =
        userJpaRepository.existsByUsername(username)

    override fun findByUsername(username: String): User? {
        val userEntity = userJpaRepository.findByUsername(username)
        return userEntity?.toDomain()
    }

    override fun findAllUnverifiedCreatedBefore(cutoff: OffsetDateTime): List<User> {
        return userJpaRepository.findByIsEmailVerifiedFalseAndCreatedAtBefore(cutoff).map { it.toDomain() }
    }

    override fun delete(user: User) {
        val entity = user.toJpaEntity()
        userJpaRepository.delete(entity)
    }

}
