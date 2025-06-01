package com.techno.valley.project2.feature.likes.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LikesService(
    private val checkIfLikeExists: CheckIfLikeExists,
    private val toggleLikeStatus: ToggleLikeStatus,
    private val createLike: CreateLike,
) {

    operator fun invoke(auth: UsersAuthentication, postId: UUID): LikesEntity {
        val existingLike = checkIfLikeExists(auth.id, postId)

        return if (existingLike != null) {
            toggleLikeStatus(existingLike)
        } else {
            createLike(auth.id, postId)
        }
    }
}
