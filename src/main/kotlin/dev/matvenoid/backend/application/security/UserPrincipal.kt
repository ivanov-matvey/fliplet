package dev.matvenoid.backend.application.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

class UserPrincipal(
    val id: UUID,
    private val phone: String,
    private val passwordHash: String,
    private val authoritiesCollection: Collection<GrantedAuthority>,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authoritiesCollection
    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = phone
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
