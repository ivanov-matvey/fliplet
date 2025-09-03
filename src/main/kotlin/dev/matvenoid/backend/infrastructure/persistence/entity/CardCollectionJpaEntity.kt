package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "card_collections")
data class CardCollectionJpaEntity(
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserJpaEntity,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var isPublic: Boolean = true,

    @Column(nullable = false)
    val createdAt: OffsetDateTime,

    @Column(nullable = false)
    val updatedAt: OffsetDateTime,
)
