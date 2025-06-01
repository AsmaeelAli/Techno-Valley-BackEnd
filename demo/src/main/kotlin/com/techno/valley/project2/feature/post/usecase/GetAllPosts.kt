package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import org.springframework.stereotype.Service

@Service
class GetAllPosts(
    private val postRepository: PostRepository,
    private val postMapper: PostDtoMapper,
) {
    operator fun invoke(): List<PostResponseDto> {
        val posts = postRepository.findAll()

        return posts.map { postMapper(it) }
    }
}

