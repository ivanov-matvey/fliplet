package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByPhone(username)
            ?: throw UsernameNotFoundException("Пользователь с телефоном $username не найден")

        return User(
            user.phone,
            user.passwordHash,
            emptyList()
        )
    }
}
