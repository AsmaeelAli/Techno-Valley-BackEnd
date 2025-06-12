package com.techno.valley.project2.feature.profilePic.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.post.usecase.FileStorageService
import com.techno.valley.project2.feature.profilePic.data.ProfilePicRepo
import com.techno.valley.project2.feature.profilePic.entity.ProfilePicEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Service
class ProfilePicService(
    private val profilePicRepo: ProfilePicRepo,
    private val snowflake: Snowflake,
    private val fileStorageService: FileStorageService,
) {

    operator fun invoke(auth: UsersAuthentication, file: MultipartFile): String {
        val userId = auth.id

        if (!isImage(file)) {
            throw IllegalArgumentException("Only image files are allowed.")
        }

        return try {
            val imageUrl = fileStorageService.store(file)
            saveProfilePic(userId, imageUrl)
            imageUrl
        } catch (e: IOException) {
            throw IOException("Error saving profile picture: ${e.message}")
        }
    }

    private fun saveProfilePic(userId: ID, imageUrl: String) {
        val profilePicEntity = ProfilePicEntity(
            id = snowflake.nextId(),
            userId = userId,
            imageUrl = imageUrl
        )
        profilePicRepo.save(profilePicEntity)
    }

    fun getProfilePicUrl(auth: UsersAuthentication): String? {
        val userId = auth.id
        return profilePicRepo.findByUserId(userId).imageUrl
    }

    private fun isImage(file: MultipartFile): Boolean {
        val allowedTypes = listOf("image/jpeg", "image/png", "image/jpg")
        return file.contentType in allowedTypes
    }
}
