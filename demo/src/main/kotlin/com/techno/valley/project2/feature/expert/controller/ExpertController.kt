package com.techno.valley.project2.feature.expert.controller

import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.expert.model.dto.ExperienceRequest
import com.techno.valley.project2.feature.expert.model.entity.ExpertEntity
import com.techno.valley.project2.feature.expert.usecase.CreateExperienceService
import com.techno.valley.project2.feature.expert.usecase.GetUserExperiencesService
import com.techno.valley.project2.feature.expert.usecase.UpdateExperienceService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/experiences")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class ExpertController(
    private val createExperienceService: CreateExperienceService,
    private val getUserExperiencesService: GetUserExperiencesService,
    private val updateExperienceService: UpdateExperienceService,
) {

    @PostMapping("new")
    @PreAuthorize("hasRole('USER')")
    fun createExperience(
        @RequestBody request: ExperienceRequest,
        auth: UsersAuthentication
    ): ResponseEntity<ExpertEntity> {
        val created = createExperienceService(auth, request.experience)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("get")
    @PreAuthorize("hasRole('USER')")
    fun getMyExperience(auth: UsersAuthentication): String = getUserExperiencesService(auth)

    @PutMapping("update")
    @PreAuthorize("hasRole('USER')")
    fun updateMyExperience(
        @RequestBody request: ExperienceRequest,
        auth: UsersAuthentication
    ): ResponseEntity<ExpertEntity> {
        val updated = updateExperienceService(auth, request.experience)
        return ResponseEntity.ok(updated)
    }
}
