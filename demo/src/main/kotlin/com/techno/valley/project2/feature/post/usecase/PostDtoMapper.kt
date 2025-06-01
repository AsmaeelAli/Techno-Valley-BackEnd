package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import com.techno.valley.project2.feature.post.model.entity.PostEntity
import com.techno.valley.project2.feature.users.data.UsersRepo
import com.techno.valley.project2.loginAuth.functions.UniversityResolver
import org.springframework.stereotype.Component

@Component
class PostDtoMapper(
    private val userRepository: UsersRepo,
    private val universityResolver: UniversityResolver
){
    operator fun invoke(post: PostEntity): PostResponseDto {
        val user = userRepository.findById(post.userId).orElseThrow {
            RuntimeException("User not found")
        }
        val universityName = universityResolver(user.email)

        return PostResponseDto(
            postId = post.id,
            username = user.name,
            university = universityName,
            content = post.content,
            fileUrl = post.fileUrl,
            imageUrl = post.imageUrl,
        )
    }
}
