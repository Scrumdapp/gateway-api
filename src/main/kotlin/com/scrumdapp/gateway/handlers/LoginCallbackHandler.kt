package com.scrumdapp.gateway.handlers

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class LoginCallbackHandler: AuthenticationSuccessHandler  {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        if (authentication is OAuth2AuthenticationToken) {
            val principal = authentication.principal as OAuth2AuthenticationToken

            if ("discord".equals(principal.authorizedClientRegistrationId, ignoreCase = true)) {
                
            }
        }

        response?.status = HttpStatus.NO_CONTENT.value()
    }

}