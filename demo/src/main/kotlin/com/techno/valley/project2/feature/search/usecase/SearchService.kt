package com.techno.valley.project2.feature.search.usecase

import com.techno.valley.project2.feature.expert.data.ExpertRepo
import com.techno.valley.project2.feature.hashtags.data.PostHashtagRepo
import com.techno.valley.project2.feature.search.model.dto.ExpertResult
import com.techno.valley.project2.feature.search.model.dto.PostResult
import com.techno.valley.project2.feature.search.model.dto.SearchGroupedResult
import com.techno.valley.project2.feature.users.data.UsersRepo
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val hashtagRepo: PostHashtagRepo,
    private val expertRepo: ExpertRepo,
    private val userRepo: UsersRepo,
) {
    operator fun invoke(query: String): SearchGroupedResult {
        val normalized = query.trim().lowercase()


        val posts = hashtagRepo.findByTagStartingWithIgnoreCase(normalized)
            .map { PostResult(it.postId, "#${it.tag}") }


        val experts = expertRepo.findByExperienceContainingIgnoreCase(normalized)
            .map { expert ->
                val user = userRepo.findById(expert.userId)
                val userName = user.get().name
                ExpertResult(userName, expert.experience)
            }

        return SearchGroupedResult(
            experts = experts,
            posts = posts,
        )
    }
}
