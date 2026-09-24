package com.scrumdapp.gateway.passports

import com.scrumdapp.gateway.security.jwt.JwtService
import com.scrumdapp.gateway.userRegistration.DownstreamRequestService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

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
        if (passport.isPresent && !passport.isEmpty) {
            return passport.get()
        }
        return generatePassport(userId)
    }

    private fun generatePassport(userId: Long): Passport {
        val claims = requestService.getPassport(userId)
        val jwtToken = jwtService.generateJwtToken(userId.toString(), claims.toJwtClaim())

        val passport = Passport(userId, passportLifeTime, jwtToken)
        return passportRepository.save(passport)
    }
}