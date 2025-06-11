package com.techno.valley.project2.feature.profilePic.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.feature.profilePic.data.ProfilePicRepo
import com.techno.valley.project2.feature.profilePic.entity.ProfilePicEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class ProfilePicService(
    private val profilePicRepo: ProfilePicRepo,
    private val snowflake: Snowflake,
) {

    // الدالة التي يمكن استدعاؤها مباشرة لرفع الصورة
    operator fun invoke(userId: ID, file: MultipartFile): String {
        return try {
            // توليد اسم فريد للصورة
            val imageUrl = saveImage(file)
            // تخزين الرابط في قاعدة البيانات
            saveProfilePic(userId, imageUrl)
            imageUrl
        } catch (e: IOException) {
            throw IOException("Error saving profile picture: ${e.message}")
        }
    }

    // رفع الصورة إلى المسار المحلي وتخزين اسم الرابط
    private fun saveImage(file: MultipartFile): String {
        val targetLocation: Path = Paths.get("uploads", file.originalFilename!!)
        Files.copy(file.inputStream, targetLocation)
        return targetLocation.toString()
    }

    // تخزين الرابط في قاعدة البيانات
    private fun saveProfilePic(userId: ID, imageUrl: String) {
        val profilePicEntity = ProfilePicEntity(
            id = snowflake.nextId(),
            userId = userId,
            imageUrl = imageUrl
        )
        profilePicRepo.save(profilePicEntity)
    }

    // جلب الصورة
    fun getProfilePic(userId: ID): ByteArray? {
        val profilePic = profilePicRepo.findById(userId)
        return if (profilePic.isPresent) {
            val imagePath = Paths.get(profilePic.get().imageUrl)
            Files.readAllBytes(imagePath)
        } else {
            null
        }
    }
}
