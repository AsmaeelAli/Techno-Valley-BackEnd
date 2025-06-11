package com.techno.valley.project2.feature.post.model.dto

import java.util.UUID

data class PostResponseDto (
    var id: UUID,
    var username: String,
    var university: String,
    var content: String,
    var hashtag: String,
    var fileUrl: String?,
    var imageUrl: String?,
    var isLiked: Boolean,
    var isSaved: Boolean,
    var numperOfLikes: Int,
)
