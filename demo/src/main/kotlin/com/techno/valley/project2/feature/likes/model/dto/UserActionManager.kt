package com.techno.valley.project2.feature.likes.model.dto

import com.techno.valley.project2.utily.ID
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserActionManager {

    private val userActionMap: MutableMap<ID, UserAction> = mutableMapOf()

    private val bannedUsers: MutableSet<ID> = mutableSetOf()

    private val maxAttempts = 8

    operator fun invoke(userId: ID, postId: UUID, actionType: String): Boolean {

        if (bannedUsers.contains(userId)) {
            return false
        }

        val currentDate = LocalDateTime.now()

        val userAction = userActionMap[userId]

        val updatedUserAction = userAction?.let {
            when (actionType) {
                "like" -> it.copy(like = it.like + 1)
                "save" -> it.copy(save = it.save + 1)
                else -> it
            }
        } ?: UserAction(1, 0, currentDate.plusDays(2))

        userActionMap[userId] = updatedUserAction

        if (updatedUserAction.like >= maxAttempts || updatedUserAction.save >= maxAttempts) {
            bannedUsers.add(userId)
            return false
        }

        return true
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun cleanBannedUsers() {
        val currentDate = LocalDateTime.now()

        bannedUsers.removeIf {
            userActionMap[it]?.blockUntil?.isBefore(currentDate) ?: false
        }
    }
}
