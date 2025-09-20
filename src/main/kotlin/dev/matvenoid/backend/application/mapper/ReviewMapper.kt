package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.study.ReviewResponse
import dev.matvenoid.backend.domain.model.CardProgress
import dev.matvenoid.backend.domain.model.Review
import org.springframework.stereotype.Component

@Component
class ReviewMapper {
    fun toResponse(
        review: Review,
        progress: CardProgress,
    ): ReviewResponse =
        ReviewResponse(
            id = review.cardId,
            quality = review.quality,
            nextReviewAt = progress.nextReviewAt.toString(),
        )
}
