package com.techno.valley.project2.config.security.model

import com.techno.valley.project2.utily.ID
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UsersAuthentication(
    val id: ID,
    private val name: String,
    private val email: String,
    private val authorities: List<SimpleGrantedAuthority>,
) : Authentication {
    override fun getName(): String = name

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        authorities.map {
            SimpleGrantedAuthority("ROLE_$it")
        }.toMutableList()

    override fun getCredentials(): Any = this

    override fun getDetails(): Any = this

    override fun getPrincipal(): Any = this

    override fun isAuthenticated(): Boolean = true

    override fun setAuthenticated(isAuthenticated: Boolean) = Unit
}
