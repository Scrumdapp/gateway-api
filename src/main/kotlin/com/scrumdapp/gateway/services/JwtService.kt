package com.scrumdapp.gateway.services

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.function.Consumer
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig {

    @Bean
    fun rsaKey(
        @Value($$"${JWT_SECRET}") jwtSecret: String
    ):RSAKey {
        return RSAKeyGenerator(2048).keyID(jwtSecret).generate()
    }

    @Bean
    fun createAsymmetricJwtEncoder(
        rsaKey: RSAKey
    ): JwtEncoder {
        val jwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(rsaKey))
        return NimbusJwtEncoder(jwkSource)
    }
}

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    @Value("\${spring.application.name}") private val applicationName: String,
) {
    fun generateJwtToken(
        subject: String, claims: Map<String, Any>): String {

        val claimSet = JwtClaimsSet.builder()
            .issuer(applicationName)
            .subject(subject)
            .expiresAt(Instant.now().plusSeconds(120))
            .claims { it.putAll(claims) }
            .build()


        val headers = JwsHeader.with(SignatureAlgorithm.RS256).build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(headers, claimSet)
        ).tokenValue
    }
}