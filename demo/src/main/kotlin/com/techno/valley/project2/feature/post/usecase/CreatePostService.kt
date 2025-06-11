package com.techno.valley.project2.feature.post.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.hashtags.data.PostHashtagRepo
import com.techno.valley.project2.feature.hashtags.model.entity.PostHashtagEntity
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.CreatePostWithFileRequest
import com.techno.valley.project2.feature.post.model.dto.PostResponse
import com.techno.valley.project2.feature.post.model.entity.PostEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
class CreatePostService(
    private val postRepository: PostRepository,
    private val postHashtagRepository: PostHashtagRepo, // إضافة Repository للهاشتاقات
    private val fileStorageService: FileStorageService,
    private val snowflake: Snowflake
) {

    companion object {
        private val SUPPORTED_IMAGE_EXTENSIONS = listOf(".png", ".jpg", ".jpeg")
        private val SUPPORTED_DOC_EXTENSIONS = listOf(".pdf", ".docx")
        private const val MAX_FILE_SIZE_MB = 10
    }

    operator fun invoke(
        request: CreatePostWithFileRequest,
        file: MultipartFile?,
        auth: UsersAuthentication,
    ): PostResponse {

        var uploadedFilePath = ""

        // تحقق من وجود ملف
        if (file != null) {
            val originalFilename =
                file.originalFilename ?: throw IllegalArgumentException("file does not have original filename")
            val lowercaseFilename = originalFilename.lowercase()

            val isDocument = SUPPORTED_DOC_EXTENSIONS.any { lowercaseFilename.endsWith(it) }
            val isImage = SUPPORTED_IMAGE_EXTENSIONS.any { lowercaseFilename.endsWith(it) }

            if (!isDocument && !isImage) {
                throw IllegalArgumentException("file does not sported")
            }

            if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
                throw IllegalArgumentException("The file size exceeds the allowed limit of (${MAX_FILE_SIZE_MB}MB).")
            }

            try {
                uploadedFilePath = fileStorageService.store(file)
            } catch (ex: Exception) {
                throw RuntimeException("An error occurred while saving the file: ${ex.message}")
            }
        }

        // إنشاء البوست
        val lowercasePath = uploadedFilePath.lowercase()
        val isDocumentPath = SUPPORTED_DOC_EXTENSIONS.any { lowercasePath.endsWith(it) }
        val isImagePath = SUPPORTED_IMAGE_EXTENSIONS.any { lowercasePath.endsWith(it) }

        val post = PostEntity(
            id = UUID.randomUUID(),
            userId = auth.id,
            content = request.content,
            fileUrl = if (isDocumentPath) uploadedFilePath else "",
            imageUrl = if (isImagePath) uploadedFilePath else "",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        postRepository.save(post)

        // تخزين الهاشتاق في جدول post_hashtags (واحد فقط)
        val hashtag = request.hashtag.trim().lowercase().removePrefix("#")
        if (hashtag.isNotBlank()) {
            val postHashtag = PostHashtagEntity(
                id = snowflake.nextId(),
                postId = post.id,
                tag = hashtag,
            )

            // حفظ الهاشتاق
            postHashtagRepository.save(postHashtag)
        }

        return PostMapper.toResponse(post)
    }
}
