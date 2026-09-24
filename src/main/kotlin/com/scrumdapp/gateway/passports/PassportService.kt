package com.scrumdapp.gateway.passports

import com.scrumdapp.gateway.security.jwt.JwtService
import com.scrumdapp.gateway.userRegistration.DownstreamRequestService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

data class GatewayToken(
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
    val passportRepository: PassportRepository,
    @Value($$"${JWT_LIFETIME}") private val passportLifeTime: Long = 60*5
) {
    fun getPassportToken(userId: Long): String {
        val passport = findPassport(userId)
        return passport.token
    }

    fun invalidatePassport(userId: Long) {
        passportRepository.deleteById(userId)
    }

    private fun findPassport(userId: Long): Passport {
        val passport = passportRepository.findById(userId)
        println("passport: $passport")
        if (passport.isPresent && !passport.isEmpty) {
            return passport.get()
        }
        return generatePassport(userId)
    }

    private fun generatePassport(userId: Long): Passport {
        val claims = requestService.getPassport(userId)
        val jwtToken = jwtService.generateJwtToken(userId.toString(), claims.toJwtClaim())

        println("Creating new passport for $userId")
        val passport = Passport(userId, passportLifeTime, jwtToken)
        return passportRepository.save(passport)
    }
}