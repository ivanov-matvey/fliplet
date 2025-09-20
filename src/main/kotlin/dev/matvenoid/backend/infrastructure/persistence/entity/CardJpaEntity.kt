package dev.matvenoid.backend.infrastructure.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "cards")
data class CardJpaEntity (
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_collection_id", nullable = false)
    val cardCollection: CardCollectionJpaEntity,

    @Column(columnDefinition = "TEXT")
    var front: String,

    @Column(columnDefinition = "TEXT")
    var back: String,

    @Column(nullable = false)
    val createdAt: OffsetDateTime,

    @Column(nullable = false)
    val updatedAt: OffsetDateTime,

    @OneToMany(mappedBy = "card", orphanRemoval = true, cascade = [CascadeType.ALL])
    val progresses: MutableList<CardProgressJpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "card", orphanRemoval = true, cascade = [CascadeType.ALL])
    val reviews: MutableList<ReviewJpaEntity> = mutableListOf(),
)
