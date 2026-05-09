package com.techno.valley.project2.feature.post.model.dto

data class AllPostsResponseDto(
    val myPosts: List<PostResponseDto>,
    val likedPosts: List<PostResponseDto>,
    val savedPosts: List<PostResponseDto>
)
