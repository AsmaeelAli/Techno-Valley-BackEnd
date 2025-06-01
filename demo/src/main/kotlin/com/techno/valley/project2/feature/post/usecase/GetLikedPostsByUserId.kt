package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import org.springframework.stereotype.Service

@Service
class GetLikedPostsByUserId(
    private val likesRepo: LikesRepo,
    private val postRepository: PostRepository,
    private val postMapper: PostDtoMapper,
) {
    operator fun invoke(auth: UsersAuthentication): List<PostResponseDto> {

        val likedPosts = likesRepo.findByUserIdAndEnableTrue(auth.id)

        val postIds = likedPosts.map { it.postId }

        val posts = postRepository.findAllById(postIds)

        return posts.map { postMapper(it) }
    }
}
