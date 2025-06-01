package com.techno.valley.project2.feature.search.controller

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.config.OpenApiConfiguration
import com.techno.valley.project2.feature.search.model.dto.SearchGroupedResult
import com.techno.valley.project2.feature.search.usecase.SearchService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = OpenApiConfiguration.SECURITY_REQUIREMENT_NAME)
class SearchController(
    private val searchService: SearchService
) {

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    fun search(@RequestParam("q") query: String): SearchGroupedResult {
        if (query.isBlank()) {
            throw RestExceptions.BadRequest("Query must not be blank")
        }

        return searchService(query)
    }
}
