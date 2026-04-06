package com.scrumdapp.gateway.services

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class JwtService {

    private fun generateKey(): SecretKey {
        val gen = Base64.getDecoder().decode("MEH")
        val key: SecretKey = SecretKeySpec(gen, 0, gen.size, "AES")
        return key
    }
    public fun generateJwt(subject: String, claims: Map<String, Any>): Jwt {
        val jwt = Jwt.Builder("meh")



        return Jwt.Builder
            .subject(subject)
            .claims { claims.toMutableMap() }
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60 * 2)) // 2 minutes
            .tokenValue(generateKey().toString())
            .build()
    }
}