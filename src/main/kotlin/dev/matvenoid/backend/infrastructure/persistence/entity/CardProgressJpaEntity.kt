package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "card_progress",
    uniqueConstraints = [UniqueConstraint(
        name = "uq_progress_card_user",
        columnNames = ["card_id", "user_id"]
    )]
)
data class CardProgressJpaEntity(
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
    var repetition: Int,

    @Column(nullable = false)
    var intervalDays: Int,

    @Column(nullable = false)
    var easeFactor: Double,

    @Column
    var lastReviewAt: OffsetDateTime?,

    @Column(nullable = false)
    var nextReviewAt: OffsetDateTime,
)
