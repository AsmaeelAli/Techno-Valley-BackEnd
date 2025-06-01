package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.post.model.dto.PostResponse
import com.techno.valley.project2.feature.post.model.entity.PostEntity

object PostMapper {
    fun toResponse(post: PostEntity): PostResponse {
        return PostResponse(
            id = post.id,
            userId = post.userId,
            content = post.content,
            fileUrl = post.fileUrl,
            imageUrl = post.imageUrl,
            createdAt = post.createdAt,
        )
    }
}
