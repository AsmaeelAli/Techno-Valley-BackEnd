package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.post.data.PostRepository
import com.techno.valley.project2.feature.post.model.dto.SearchGroupedPostResponse
import com.techno.valley.project2.feature.saves.data.SavesRepo
import com.techno.valley.project2.feature.search.usecase.SearchService
import org.springframework.stereotype.Service

@Service
class GetHashtagedPostsService(
    private val searchService: SearchService,
    private val postRepo: PostRepository,
    private val saveRepo: SavesRepo,
    private val likesRepo: LikesRepo,
    private val dtoMapper: PostDtoMapper
) {
    operator fun invoke(auth: UsersAuthentication, hashtag: String): SearchGroupedPostResponse {
        val grouped = searchService(hashtag)

        print("Hellllllllllllllllllllllloooooooooooooo")

        val posts = grouped.posts.mapNotNull { postResult ->
            val post = postRepo.findById(postResult.postId).orElse(null) ?: return@mapNotNull null
            val isLiked = likesRepo.existsByUserIdAndPostIdAndEnableTrue(auth.id, post.id)
            val isSaved = saveRepo.existsByUserIdAndPostIdAndEnableTrue(auth.id, post.id)
            dtoMapper(post, isLiked, isSaved)
        }

        val experts = grouped.experts

        return SearchGroupedPostResponse(posts = posts, experts = experts)
    }
}