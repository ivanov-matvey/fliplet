package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.security.UserPrincipal
import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException(
                "Пользователь с электронной почтой $username не найден"
            )

        return UserPrincipal(
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash,
            authoritiesCollection = emptyList()
        )
    }
}
