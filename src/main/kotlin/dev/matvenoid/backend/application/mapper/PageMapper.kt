package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.PageResponse
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class PageMapper {
    fun <T> toResponse(entity: Page<T>): PageResponse<T> =
        PageResponse(
            content = entity.content,
            page = entity.number,
            size = entity.size,
            totalPages = entity.totalPages,
            totalElements = entity.totalElements,
            first = entity.isFirst,
            last = entity.isLast,
            hasNext = entity.hasNext(),
            hasPrevious = entity.hasPrevious()
        )
}