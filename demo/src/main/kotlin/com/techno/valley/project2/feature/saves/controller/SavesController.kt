package com.techno.valley.project2.feature.saves.controller

import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import com.techno.valley.project2.feature.saves.usecase.SavesService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/saves")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class SavesController(
    private val savesService: SavesService,
) {
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/toggle")
    fun newSave(auth: UsersAuthentication, @RequestParam postId: UUID): SavesEntity = savesService(auth, postId)
}