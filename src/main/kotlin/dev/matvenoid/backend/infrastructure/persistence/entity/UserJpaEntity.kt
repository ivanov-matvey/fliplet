package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
data class UserJpaEntity(
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(nullable = false, length = 32)
    var username: String,

    @Column(nullable = false, unique = true, length = 32)
    var usernameCi: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, unique = true, length = 10)
    var phone: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var avatarUrl: String? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var passwordHash: String,

    @CreationTimestamp
    @Column(nullable = false)
    val createdAt: OffsetDateTime,

    @UpdateTimestamp
    @Column(nullable = false)
    val updatedAt: OffsetDateTime,
) {
    @PrePersist
    @PreUpdate
    fun normalize() {
        usernameCi = username.lowercase()
    }
}