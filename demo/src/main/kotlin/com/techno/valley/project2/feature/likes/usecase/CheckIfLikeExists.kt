package com.techno.valley.project2.feature.likes.usecase

import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CheckIfLikeExists(
    private val likesRepo: LikesRepo,
) {
    operator fun invoke(userId: ID, postId: UUID): LikesEntity? {
        return likesRepo.findByUserIdAndPostId(userId, postId)
    }
}
