package com.scrumdapp.gateway.controllers

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class GatewayController(
    private val rsaKey: RSAKey
) {

    @GetMapping("/login")
    fun loginRedirect(): RedirectView {
        return RedirectView("oauth2/authorization/discord")
    }

    @GetMapping("/logoutsuccessful")
    fun logoutRedirect(): ApiResponse {
        val body = ApiResponse(
            code = HttpStatus.OK.value(),
            message = "Logout successful"
        )
        return body
    }

    @GetMapping("/.well-known/jwks.json")
    fun keys(): Map<String, Any> {
        val jwk = rsaKey.toPublicJWK().toJSONObject()
        return jwk
    }
}