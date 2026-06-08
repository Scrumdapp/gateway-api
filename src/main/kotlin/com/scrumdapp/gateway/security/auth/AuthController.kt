package com.scrumdapp.gateway.security.auth

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.scrumdapp.gateway.exceptions.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class AuthController(
    private val rsaKey: RSAKey
) {

    @GetMapping("/login")
    fun loginRedirect(): RedirectView {
        return RedirectView("api/oauth2/authorization/discord")
    }

    @GetMapping("/.well-known/jwks.json")
    fun keys(): Map<String, Any> {
        val jwk = JWKSet(rsaKey).toPublicJWKSet().toJSONObject()

        return jwk
    }
}