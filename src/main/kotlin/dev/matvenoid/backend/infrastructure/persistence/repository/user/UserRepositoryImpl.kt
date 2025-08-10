package dev.matvenoid.backend.infrastructure.persistence.repository.user

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import dev.matvenoid.backend.infrastructure.persistence.mapper.toDomain
import dev.matvenoid.backend.infrastructure.persistence.mapper.toJpaEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun existsByPhone(phone: String): Boolean =
        userJpaRepository.existsByPhone(phone)

    override fun findByPhone(phone: String): User? {
        val userEntity = userJpaRepository.findByPhone(phone)
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

    override fun existsByUsernameCi(usernameCi: String) =
        userJpaRepository.existsByUsernameCi(usernameCi)

    override fun findByUsernameCi(usernameCi: String): User? {
        val userEntity = userJpaRepository.findByUsernameCi(usernameCi)
        return userEntity?.toDomain()
    }
}
