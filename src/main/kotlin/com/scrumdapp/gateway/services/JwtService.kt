package com.scrumdapp.gateway.services

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.function.Consumer
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig {

    @Bean
    fun jwtEncoder(
        @Value($$"${JWT_SECRET}") secret: String
    ): JwtEncoder {
        val secret = secret.toByteArray()
        val key = SecretKeySpec(secret, "HmacSHA256")
        return NimbusJwtEncoder(ImmutableSecret(key))
    }
}

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    @Value("\${spring.application.name}") private val applicationName: String,
) {
     fun generateJwtToken(
        subject: String, claims: Map<String, Any>): String {

        val jwtClaim = JwtClaimsSet.builder()
            .issuer(applicationName)
            .subject(subject)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60 * 2))
            .claims { it.putAll(claims) }
            .build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()

        val encoderParams = JwtEncoderParameters.from(headers, jwtClaim)
        val jwt = jwtEncoder.encode(encoderParams )

        return jwt.tokenValue
    }
}