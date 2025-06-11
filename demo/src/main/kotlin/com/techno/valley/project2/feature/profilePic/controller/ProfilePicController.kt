package com.techno.valley.project2.feature.profilePic.controller

import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.feature.profilePic.usecase.ProfilePicService
import com.techno.valley.project2.utily.ID
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class ProfilePicController(
    private val profilePicService: ProfilePicService
) {

    // Endpoint لتحميل صورة الملف وتخزينها
    @PostMapping("/upload")
    fun uploadProfilePic(
        @RequestParam("userId") userId: ID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        return try {
            //profilePicService.uploadProfilePic(userId, file)
            ResponseEntity.status(HttpStatus.CREATED).body("Profile picture uploaded successfully")
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error uploading profile picture: ${e.message}")
        }
    }

    // Endpoint لجلب صورة الملف
    @GetMapping("/{userId}/picture")
    fun getProfilePic(@PathVariable userId: ID): ResponseEntity<ByteArray> {
        val image = profilePicService.getProfilePic(userId)
        return if (image != null) {
            ResponseEntity.ok().body(image)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}
