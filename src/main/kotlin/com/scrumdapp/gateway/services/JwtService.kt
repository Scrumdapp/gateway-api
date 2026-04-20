package com.scrumdapp.gateway.services

import com.netflix.appinfo.ApplicationInfoManager
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    private val applicationInfoManager: ApplicationInfoManager,
) {
    fun generateJwtToken(
        subject: String, claims: Map<String, Any>): String {

        val claimSet = JwtClaimsSet.builder()
            .issuer(applicationInfoManager.info.homePageUrl)
            .subject(subject)
            .expiresAt(Instant.now().plusSeconds(60*5))
            .claims { it.putAll(claims) }
            .build()

        val headers = JwsHeader.with(SignatureAlgorithm.RS256).build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(headers, claimSet)
        ).tokenValue
    }
}