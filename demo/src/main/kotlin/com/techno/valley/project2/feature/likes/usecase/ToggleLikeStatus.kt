package com.techno.valley.project2.feature.likes.usecase

import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.likes.model.dto.UserActionManager
import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import org.springframework.stereotype.Service

@Service
class ToggleLikeStatus(
    private val likesRepo: LikesRepo,
    private val userActionManager: UserActionManager,
) {
    operator fun invoke(like: LikesEntity): LikesEntity {

        val result = userActionManager(like.userId, like.postId, "like")

        if (!result) {
            return like
        }

        return try {
            like.apply {
                enable = !enable
            }.let {
                likesRepo.save(it)
            }
        } catch (e: Exception) {
            throw RuntimeException("Error while toggling like status", e)
        }
    }
}
