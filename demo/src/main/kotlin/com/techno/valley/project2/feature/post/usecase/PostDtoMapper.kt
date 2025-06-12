package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.hashtags.data.PostHashtagRepo
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import com.techno.valley.project2.feature.post.model.entity.PostEntity
import com.techno.valley.project2.feature.profilePic.data.ProfilePicRepo
import com.techno.valley.project2.feature.users.data.UsersRepo
import com.techno.valley.project2.loginAuth.functions.UniversityResolver
import org.springframework.stereotype.Component

@Component
class PostDtoMapper(
    private val userRepository: UsersRepo,
    private val universityResolver: UniversityResolver,
    private val hashtagRepo: PostHashtagRepo,
    private val likesRepo: LikesRepo,
    private val profilePicRepo: ProfilePicRepo
) {
    operator fun invoke(post: PostEntity, isLiked: Boolean, isSaved: Boolean): PostResponseDto {
        val user = userRepository.findById(post.userId).orElseThrow {
            RuntimeException("User not found")
        }
        val universityName = universityResolver(user.email)

        val postHashtag = hashtagRepo.findByPostId(post.id)

        val counter = likesRepo.countByPostIdAndEnableTrue(post.id)

        return PostResponseDto(
            id = post.id,
            username = user.name,
            university = universityName,
            content = post.content,
            hashtag = postHashtag.tag,
            fileUrl = post.fileUrl,
            imageUrl = post.imageUrl,
            profileUrl = profilePicRepo.findByUserId(user.id).imageUrl,
            isLiked = isLiked,
            isSaved = isSaved,
            numberOfLikes = counter,
        )
    }
}
