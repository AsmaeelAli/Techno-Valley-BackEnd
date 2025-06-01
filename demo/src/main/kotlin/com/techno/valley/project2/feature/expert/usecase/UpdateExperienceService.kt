package com.techno.valley.project2.feature.expert.usecase

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.expert.data.ExpertRepo
import com.techno.valley.project2.feature.expert.model.entity.ExpertEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UpdateExperienceService(
    private val expertRepo: ExpertRepo
) {
    @Transactional
    operator fun invoke(auth: UsersAuthentication, newExperience: String): ExpertEntity {
        val existing = expertRepo.findByUserId(auth.id)
            ?: throw RestExceptions.NotFound("No experiences found for this user.")

        val oldExperiences = existing.experience
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }

        val trimmedNewExp = newExperience.trim()

        // التحقق من وجود الخبرة مسبقًا (بحساسية حالة)
        if (oldExperiences.contains(trimmedNewExp)) {
            return existing
        }

        // دمج الخبرة الجديدة في البداية
        val sb = StringBuilder()
        sb.append(trimmedNewExp)
        if (oldExperiences.isNotEmpty()) {
            sb.append(" ")
            sb.append(oldExperiences.joinToString(" "))
        }

        val updated = existing.copy(experience = sb.toString())
        return expertRepo.save(updated)
    }
}
