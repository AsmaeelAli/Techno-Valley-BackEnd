package com.techno.valley.project2.feature.users.data

import com.techno.valley.project2.feature.users.model.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UsersRepo : JpaRepository<UsersEntity, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmailIgnoreCase(email: String): Optional<UsersEntity>
}
