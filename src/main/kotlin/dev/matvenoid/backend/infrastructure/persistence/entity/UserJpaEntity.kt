package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.OneToMany
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
data class UserJpaEntity(
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    val id: UUID,

    @Column(nullable = false, unique = true, columnDefinition = "citext")
    var username: String,

    @Column(nullable = true, length = 100)
    var name: String?,

    @Column(nullable = false, unique = true, columnDefinition = "citext")
    var email: String,

    @Column(nullable = true, unique = true, columnDefinition = "citext")
    var pendingEmail: String?,

    @Column(nullable = false, columnDefinition = "TEXT")
    var avatarUrl: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var passwordHash: String,

    @Column(nullable = false)
    var isEmailVerified: Boolean = false,

    @Column(nullable = false)
    val createdAt: OffsetDateTime,

    @Column(nullable = false)
    val updatedAt: OffsetDateTime,

    @Column
    val pendingEmailRequestedAt: OffsetDateTime? = null,

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = [CascadeType.ALL])
    val collections: MutableList<CardCollectionJpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = [CascadeType.ALL])
    val cardProgresses: MutableList<CardProgressJpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", orphanRemoval = true,  cascade = [CascadeType.ALL])
    val reviews: MutableList<ReviewJpaEntity> = mutableListOf(),
)