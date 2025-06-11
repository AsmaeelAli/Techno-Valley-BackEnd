package com.techno.valley.project2.feature.saves.usecase

import com.techno.valley.project2.feature.likes.model.dto.UserActionManager
import com.techno.valley.project2.feature.saves.data.SavesRepo
import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import org.springframework.stereotype.Service

@Service
class ToggleSaveStatus(
    private val savesRepo: SavesRepo,
    private val userActionManager: UserActionManager,
) {
    operator fun invoke(save: SavesEntity): SavesEntity {

        val result = userActionManager(save.userId, save.postId, "save")

        if (!result) {
            return save
        }

        return try {
            save.apply {
                enable = !enable
            }.let {
                savesRepo.save(it)
            }
        } catch (e: Exception) {
            throw RuntimeException("Error while toggling save status", e)
        }
    }
}
