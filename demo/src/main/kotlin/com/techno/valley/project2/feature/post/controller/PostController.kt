package com.techno.valley.project2.feature.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.post.model.dto.CreatePostWithFileRequest
import com.techno.valley.project2.feature.post.model.dto.PostResponse
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import com.techno.valley.project2.feature.post.usecase.CreatePostService
import com.techno.valley.project2.feature.post.usecase.GetAllPosts
import com.techno.valley.project2.feature.post.usecase.GetLikedPostsByUserId
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/posts")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class PostController(
    private val createPostService: CreatePostService,
    private val objectMapper: ObjectMapper,
    private val getAllPostsService: GetAllPosts,
    private val getLikedPostsByUserIdService: GetLikedPostsByUserId,
) {

    private val logger = LoggerFactory.getLogger(PostController::class.java)

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPost(
        auth: UsersAuthentication,
        @RequestPart("data") requestJson: String,
        @RequestPart("file", required = false) file: MultipartFile?
    ): ResponseEntity<PostResponse> {
        return try {
            // تحويل JSON إلى DTO
            val request = objectMapper.readValue(requestJson, CreatePostWithFileRequest::class.java)

            // استدعاء الخدمة
            val response = createPostService(request, file, auth)

            // إرجاع النتيجة
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.warn("Invalid input: {}", e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    fun getAllPosts(auth: UsersAuthentication): List<PostResponseDto> = getAllPostsService(auth)



    @PreAuthorize("hasRole('USER')")
    @GetMapping("/liked")
    fun likedPosts(auth: UsersAuthentication): List<PostResponseDto> = getLikedPostsByUserIdService(auth)
}
