package com.techno.valley.project2.feature.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.post.model.dto.AllPostsResponseDto
import com.techno.valley.project2.feature.post.model.dto.CreatePostWithFileRequest
import com.techno.valley.project2.feature.post.model.dto.PostResponseDto
import com.techno.valley.project2.feature.post.model.dto.SearchGroupedPostResponse
import com.techno.valley.project2.feature.post.usecase.CreatePostService
import com.techno.valley.project2.feature.post.usecase.GetAllPosts
import com.techno.valley.project2.feature.post.usecase.GetAllPostsService
import com.techno.valley.project2.feature.post.usecase.GetHashtagedPostsService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/posts")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class PostController(
    private val createPostService: CreatePostService,
    private val objectMapper: ObjectMapper,
    private val getAllPosts: GetAllPosts,
    private val getAllPostsService: GetAllPostsService,
    private val getHashtagedPostsService: GetHashtagedPostsService,
) {

    private val logger = LoggerFactory.getLogger(PostController::class.java)

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPost(
        auth: UsersAuthentication,
        @RequestPart("data") requestJson: String,
        @RequestPart("file", required = false) file: MultipartFile?
    ): ResponseEntity<Any> {
        return try {
            val request = objectMapper.readValue(requestJson, CreatePostWithFileRequest::class.java)
            val response = createPostService(request, file, auth)
            ResponseEntity.ok(response)
        } catch (e: RestExceptions.Forbidden) {
            logger.warn("Forbidden: {}", e.message)
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            logger.error("Internal error: {}", e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "Internal server error"))
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    fun getAll(auth: UsersAuthentication): List<PostResponseDto> = getAllPosts(auth)


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    fun getAllUserPosts(auth: UsersAuthentication): AllPostsResponseDto = getAllPostsService(auth)

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hashtag")
    fun getAllHashtaged(
        auth: UsersAuthentication,
        @RequestParam hashtag: String
    ): SearchGroupedPostResponse {
        return getHashtagedPostsService(auth, hashtag)
    }
}
