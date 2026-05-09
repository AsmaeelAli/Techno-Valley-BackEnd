package com.techno.valley.project2.feature.search.model.dto

import java.util.*

data class SearchGroupedResult(
    val experts: List<ExpertResult>,
    val posts: List<PostResult>
)

data class ExpertResult(
    val username: String,
    val experience: String
)

data class PostResult(
    val postId: UUID,
    val tag: String
)
