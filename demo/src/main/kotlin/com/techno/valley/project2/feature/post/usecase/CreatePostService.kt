package com.techno.valley.project2.feature.post.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.BannedWords
import com.techno.valley.project2.config.security.config.FileScannerService
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.hashtags.data.PostHashtagRepo
import com.techno.valley.project2.feature.hashtags.model.entity.PostHashtagEntity
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.CreatePostWithFileRequest
import com.techno.valley.project2.feature.post.model.dto.PostResponse
import com.techno.valley.project2.feature.post.model.entity.PostEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Service
class CreatePostService(
    private val postRepository: PostRepository,
    private val postHashtagRepository: PostHashtagRepo,
    private val fileStorageService: FileStorageService,
    private val fileScannerService: FileScannerService,
    private val snowflake: Snowflake,
    private val bannedWords: BannedWords,
) {

    companion object {
        private val SUPPORTED_IMAGE_EXTENSIONS = listOf(".png", ".jpg", ".jpeg")
        private val SUPPORTED_DOC_EXTENSIONS = listOf(
            ".pdf", ".docx", ".xlsx", ".pptx", ".txt", ".zip", ".rar"
        )
        private const val MAX_FILE_SIZE_MB = 100
    }

    operator fun invoke(
        request: CreatePostWithFileRequest,
        file: MultipartFile?,
        auth: UsersAuthentication,
    ): PostResponse {

        var uploadedFilePath = ""

        if (file != null) {
            if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
                throw RestExceptions.Forbidden("The file size exceeds the maximum limit")
            }

            val cleanFile: File = fileScannerService.scanFile(file, auth)
                ?: throw RestExceptions.Forbidden("You want to play? Let's play")

            val filename = cleanFile.name.lowercase()
            val isDocument = SUPPORTED_DOC_EXTENSIONS.any { filename.endsWith(it) }
            val isImage = SUPPORTED_IMAGE_EXTENSIONS.any { filename.endsWith(it) }

            if (!isDocument && !isImage) {
                cleanFile.delete()
                throw RestExceptions.Forbidden("You want to play? Let's play")
            }

            try {
                uploadedFilePath = fileStorageService.store(cleanFile)
            } catch (ex: Exception) {
                cleanFile.delete()
                throw RuntimeException("An error occurred while saving the file: \${ex.message}")
            }

            cleanFile.delete()
        }

        val contentLower = request.content.trim().lowercase()
        val hashtagLower = request.hashtag.trim().lowercase()
        val allInput = "$contentLower $hashtagLower"

        val matchedBanned = bannedWords.words.firstOrNull { word ->
            allInput.contains(word)
        }

        if (matchedBanned != null) {
            throw RestExceptions.Forbidden("Banned word used")
        }

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

        val hashtag = hashtagLower.removePrefix("#")
        if (hashtag.isNotBlank()) {
            val postHashtag = PostHashtagEntity(
                id = snowflake.nextId(),
                postId = post.id,
                tag = hashtag,
            )
            postHashtagRepository.save(postHashtag)
        }

        return PostMapper.toResponse(post)
    }
}
