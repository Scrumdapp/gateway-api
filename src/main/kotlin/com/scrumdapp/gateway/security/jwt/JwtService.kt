package com.scrumdapp.gateway.security.jwt

import com.nimbusds.jwt.JWTParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,

    @Value($$"${spring.application.name}") private val appName: String,
) {
    fun generateJwtToken(
        subject: String,
        claims: Map<String, Any>): String {

        val claimSet = JwtClaimsSet.builder()
            .issuer("localhost:9999")
            .subject(subject)
            .expiresAt(Instant.now().plusSeconds(60*5))
            .claims { it.putAll(claims) }
            .build()

        val headers = JwsHeader.with(SignatureAlgorithm.RS256).build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(headers, claimSet)
        ).tokenValue
    }

    fun extractClaims(token: String): Map<String, Any> {

        val claims = JWTParser.parse(token)
        return claims.jwtClaimsSet.claims
    }
}