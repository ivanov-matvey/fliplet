package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "reviews")
data class ReviewJpaEntity(
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    val card: CardJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserJpaEntity,

    @Column(nullable = false)
    val reviewedAt: OffsetDateTime,

    @Column(nullable = false)
    val quality: Short,

    @Column(nullable = false)
    val prevIntervalDays: Int,

    @Column(nullable = false)
    val newIntervalDays: Int,

    @Column(nullable = false)
    val prevEaseFactor: Double,

    @Column(nullable = false)
    val newEaseFactor: Double,

    @Column(nullable = false)
    val prevRepetitions: Int,

    @Column(nullable = false)
    val newRepetitions: Int,
)
