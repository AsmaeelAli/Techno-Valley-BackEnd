package com.techno.valley.project2.feature.profilePic.controller

import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.profilePic.usecase.ProfilePicService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class ProfilePicController(
    private val profilePicService: ProfilePicService
) {

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfilePic(
        auth: UsersAuthentication,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        return try {
            val imageUrl = profilePicService(auth, file)
            ResponseEntity.status(HttpStatus.CREATED).body(imageUrl)
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error uploading profile picture: ${e.message}")
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/picture")
    fun getProfilePic(auth: UsersAuthentication): ResponseEntity<String> {
        val imageUrl = profilePicService.getProfilePicUrl(auth)
        return if (imageUrl != null) {
            ResponseEntity.ok().body(imageUrl)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}
