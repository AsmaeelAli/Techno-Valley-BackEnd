package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.AllPostsResponseDto
import com.techno.valley.project2.feature.saves.data.SavesRepo
import org.springframework.stereotype.Service

@Service
class GetAllPostsService(
    private val postRepository: PostRepository,
    private val likesRepo: LikesRepo,
    private val savesRepo: SavesRepo,
    private val postMapper: PostDtoMapper
) {

    operator fun invoke(auth: UsersAuthentication): AllPostsResponseDto {
        val userId = auth.id

        val myPosts = postRepository.findByUserId(userId).map {
            val isLiked = likesRepo.existsByUserIdAndPostIdAndEnableTrue(userId, it.id)
            val isSaved = savesRepo.existsByUserIdAndPostIdAndEnableTrue(userId, it.id)
            postMapper(it, isLiked, isSaved)
        }

        val likedPostIds = likesRepo.findByUserIdAndEnableTrue(userId).map { it.postId }
        val likedPosts = postRepository.findAllById(likedPostIds).map {
            val isLiked = true
            val isSaved = savesRepo.existsByUserIdAndPostIdAndEnableTrue(userId, it.id)
            postMapper(it, isLiked, isSaved)
        }

        val savedPostIds = savesRepo.findByUserIdAndEnableTrue(userId).map { it.postId }
        val savedPosts = postRepository.findAllById(savedPostIds).map {
            val isLiked = likesRepo.existsByUserIdAndPostIdAndEnableTrue(userId, it.id)
            val isSaved = true
            postMapper(it, isLiked, isSaved)
        }

        return AllPostsResponseDto(
            myPosts = myPosts,
            likedPosts = likedPosts,
            savedPosts = savedPosts
        )
    }
}
