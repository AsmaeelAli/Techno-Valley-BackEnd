package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import org.springframework.stereotype.Service

@Service
class GetAllPosts(
    private val postRepository: PostRepository,
    private val postMapper: PostDtoMapper,
    private val likesRepo: LikesRepo,
    private val saveRepo: SaveRepo,
) {
    operator fun invoke(auth: UsersAuthentication): List<PostResponseDto> {
        val posts = postRepository.findAll()

        return posts.map { post ->
            val isLiked = likesRepo.existsByUserIdAndPostIdAndEnableTrue(auth.id, post.id)
            val isSaved = saveRepo.existsByUserIdAndPostIdAndEnableTrue(auth.id, post.id)

            // تعديل الـ Mapper لإضافة حالة الإعجاب والحفظ
            postMapper(post, isLiked, isSaved)
        }
    }
}

