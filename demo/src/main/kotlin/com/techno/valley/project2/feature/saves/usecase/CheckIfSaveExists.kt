package com.techno.valley.project2.feature.saves.usecase

import com.techno.valley.project2.feature.saves.data.SavesRepo
import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Component
import java.util.*

@Component
class CheckIfSaveExists(
    private val savesRepo: SavesRepo,
) {
    operator fun invoke(userId: ID, postId: UUID): SavesEntity? {
        return savesRepo.findByUserIdAndPostId(userId, postId)
    }
}