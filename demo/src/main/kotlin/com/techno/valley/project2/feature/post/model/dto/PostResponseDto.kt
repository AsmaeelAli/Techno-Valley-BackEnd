package com.techno.valley.project2.feature.post.model.dto

import java.util.UUID

data class PostResponseDto (
    var postId: UUID,
    var username: String,
    var university: String,
    var content: String,
    var hashtag: String,
    var fileUrl: String?,
    var imageUrl: String?,
    var isLiked: Boolean,  // إضافة حالة الإعجاب
    var isSaved: Boolean   // إضافة حالة الحفظ
)
