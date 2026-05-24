package com.scrumdapp.gateway.passports

import com.scrumdapp.gateway.security.jwt.JwtService
import com.scrumdapp.gateway.services.DownstreamRequestService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

data class PassportToken(
    val token: String,
    val expiresAt: Instant
) {
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}

data class PassportContent(
    val userId: Int,
    val userGroups: List<Int> = listOf(),
    val roles: List<String>
) {
    fun toJwtClaim(): Map<String, Any> {
        return mapOf("userId" to userId, "userGroups" to userGroups, "roles" to roles)
    }
}

@Service
class PassportService(
    val jwtService: JwtService,
    val requestService: DownstreamRequestService,

    @Value($$"${JWT_LIFETIME}") private val passportLifeTime: Long = 60*5
) {
    fun generatePassport(userId: Long): PassportToken {

        val expiresAt = Instant.now().plusSeconds(passportLifeTime)

        val claims = requestService.getPassport(userId)

        val token = jwtService.generateJwtToken(
            subject = userId.toString(),
            claims = claims.toJwtClaim()
        )

        return PassportToken(token, expiresAt)
    }
}