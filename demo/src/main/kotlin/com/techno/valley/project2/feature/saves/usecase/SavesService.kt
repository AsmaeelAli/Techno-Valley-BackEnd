package com.techno.valley.project2.feature.saves.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class SavesService(
    private val checkIfSaveExists: CheckIfSaveExists,
    private val creatSave: CreatSave,
    private val toggleSaveStatus: ToggleSaveStatus,
) {
    operator fun invoke(auth: UsersAuthentication, postId: UUID): SavesEntity {

        val existingLike = checkIfSaveExists(auth.id, postId)

        return if (existingLike != null) {
            toggleSaveStatus(existingLike)
        } else {
            creatSave(auth.id, postId)
        }
    }
}
