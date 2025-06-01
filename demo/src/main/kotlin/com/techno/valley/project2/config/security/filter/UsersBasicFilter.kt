package com.techno.valley.project2.config.security.filter

import com.techno.valley.project2.config.security.keys.PublicKeyStore
import com.techno.valley.project2.config.security.model.UsersAuthentication
import io.jsonwebtoken.Jwts
import jakarta.annotation.Priority
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.util.Date

@Component
@Priority(0)
class UsersBasicFilter(
    private val publicKeyStore: PublicKeyStore,
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val res = response as HttpServletResponse
        val path = httpRequest.requestURI

        println("Requested path: $path")

        val remoteAddr = request.remoteAddr
        if (remoteAddr == "192.168.129.235" ||
            remoteAddr == "192.168.1.103:8080" ||
            remoteAddr == "192.168.1.103" ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/api/auth/login") ||
            path.startsWith("/api/email/request") ||
            path.startsWith("/api/email/verify")||
            path.startsWith("/api/email/resend")
        ) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = httpRequest.getHeader(HttpHeaders.AUTHORIZATION)?.removePrefix("Bearer ")

        if (jwtToken.isNullOrEmpty()) {
            FilterUtils.respondWithError(
                res,
                HttpStatus.UNAUTHORIZED,
                "Access Denied: Incomplete User Info"
            )
            return
        }


        val result = try {
            Jwts.parser()
                .verifyWith(publicKeyStore.getPublicKey())
                .build()
                .parseSignedClaims(jwtToken)
        } catch (ex: Exception) {
            FilterUtils.respondWithError(
                res,
                HttpStatus.UNAUTHORIZED,
                "Access Denied: Invalid Token"
            )
            return
        }

        if (result.payload.expiration.before(Date())) {
            FilterUtils.respondWithError(
                res,
                HttpStatus.FORBIDDEN,
                "Access Denied: Token Expired"
            )
            return
        }

        val role = result.payload["role"] as? String
        if (role == null) {
            FilterUtils.respondWithError(
                res,
                HttpStatus.FORBIDDEN,
                "Access Denied: No Valid Role"
            )
            return
        }

        val userId = result.payload.id.toLong()
        val username = result.payload.subject
        val useremail = result.payload["email"] as? String ?: ""

        val authorities = when (role) {
            "USER" -> listOf(SimpleGrantedAuthority("USER"))
            "ADMIN" -> listOf(SimpleGrantedAuthority("ADMIN"))
            else -> emptyList()
        }

        SecurityContextHolder.getContext().authentication = UsersAuthentication(
            id = userId,
            name = username,
            email = useremail,
            authorities = authorities
        )
        filterChain.doFilter(request, response)
    }
}
