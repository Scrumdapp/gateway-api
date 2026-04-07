package com.scrumdapp.gateway.services

import com.nimbusds.jose.jwk.source.ImmutableSecret
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
    fun jwtEncoder(): JwtEncoder {
        val secret = "c2hvd2JyaW5ndmVnZXRhYmxlaW1wb3J0YW50ZXhwZXI=".toByteArray()
        val key = SecretKeySpec(secret, "HmacSHA256")
        return NimbusJwtEncoder(ImmutableSecret(key))
    }
}

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
) {
     fun generateJwtToken(
        subject: String, claims: Map<String, Any>): String {

        val jwtClaim = JwtClaimsSet.builder()
            .issuer("gateway")
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