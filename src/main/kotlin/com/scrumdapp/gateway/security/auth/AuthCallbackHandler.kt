package com.scrumdapp.gateway.security.auth

import com.scrumdapp.gateway.exceptions.ApplicationAuthenticationException
import com.scrumdapp.gateway.exceptions.ApplicationException
import com.scrumdapp.gateway.exceptions.BadRequestException
import com.scrumdapp.gateway.exceptions.NotAuthorizedException
import com.scrumdapp.gateway.userRegistration.UserRegistrationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class AuthCallbackHandler(
    private val userRegistrationService: UserRegistrationService,
    private val failureHandler: AuthenticationFailureHandler
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val oauth2Auth = authentication as OAuth2AuthenticationToken

        val principal = oauth2Auth.principal

        try {

            if (principal == null || principal.getAttribute<Boolean>("email_verified") == false) {
                throw NotAuthorizedException(message = "You're not authorized to access this resource")
            }

            val groups = principal.getAttribute<ArrayList<String>>("groups") ?: throw BadRequestException(message = "Authentication token doesn't contain userGroup scope")
            val email = principal.getAttribute<String>("email") ?: throw BadRequestException(message = "Authentication token doesn't contain profile scope")
            val name = principal.getAttribute<String>("name") ?: throw BadRequestException(message = "Authentication token doesn't contain profile scope")

            val userId = userRegistrationService.handleLogin(email, groups, name)
            
            val session = request.getSession(true)
            session.setAttribute("userId", userId)

            response.sendRedirect("/")
        } catch (ex: ApplicationException) {
            invalidateSession(request)

            failureHandler.onAuthenticationFailure(
                request,
                response,
                ApplicationAuthenticationException(ex)
            )
        }
    }

    fun invalidateSession(req: HttpServletRequest) {
        SecurityContextHolder.clearContext()
        req.session.invalidate()
    }
}