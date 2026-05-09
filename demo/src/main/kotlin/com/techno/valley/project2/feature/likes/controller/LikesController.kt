package com.techno.valley.project2.feature.likes.controller

import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import com.techno.valley.project2.feature.likes.usecase.LikesService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api/likes")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class LikesController(
    private val likesService: LikesService,
) {
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/toggle")
    fun newLike(
        auth: UsersAuthentication,
        @RequestParam postId: UUID,
    ): LikesEntity = likesService(auth, postId)
}
