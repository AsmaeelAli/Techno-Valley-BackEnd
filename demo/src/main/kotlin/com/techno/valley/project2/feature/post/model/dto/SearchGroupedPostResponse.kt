package com.techno.valley.project2.feature.post.model.dto

import com.techno.valley.project2.feature.search.model.dto.ExpertResult

data class SearchGroupedPostResponse(
    val posts: List<PostResponseDto>,
    val experts: List<ExpertResult>
)