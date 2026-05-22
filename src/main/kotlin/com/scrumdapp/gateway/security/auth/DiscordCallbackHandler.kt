package com.scrumdapp.gateway.security.auth

import com.scrumdapp.gateway.discordUser.DiscordService
import com.scrumdapp.gateway.discordUser.DiscordUserService
import com.scrumdapp.gateway.exceptions.ApiResponse
import com.scrumdapp.gateway.exceptions.ApplicationAuthenticationException
import com.scrumdapp.gateway.exceptions.ApplicationException
import com.scrumdapp.gateway.exceptions.NoAccessException
import com.scrumdapp.gateway.exceptions.NotAuthorizedException
import com.scrumdapp.gateway.exceptions.ServerFaultException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Component
class DiscordCallbackHandler(
    private val failureHandler: AuthenticationFailureHandler,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val discordUserService: DiscordUserService
): AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = authentication as? OAuth2AuthenticationToken
        try {
            if (token != null && "discord".equals(token.authorizedClientRegistrationId, ignoreCase = true)) {
                val client: OAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
                    token.authorizedClientRegistrationId,
                    token.name
                )

                if (client.refreshToken == null) throw NotAuthorizedException(message = "Unauthorized, no refresh token provided")

                val userId = discordUserService.handleLogin(client.accessToken.tokenValue)

                val session = request.getSession(true)
                session.setAttribute("userId", userId)

                response.status = HttpStatus.OK.value()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, ApiResponse(code = 200, "login successful"))
//                response.sendRedirect("http://localhost:5173/groups")
            } else {
                throw NoAccessException(message = "Your Discord account is not authorized to access this resource")
            }
        } catch (exception: ApplicationException) {

            println(exception.message)
            if (exception is NoAccessException) {
                SecurityContextHolder.clearContext()
                request.session.invalidate()
            }

            failureHandler.onAuthenticationFailure(
                request,
                response,
                ApplicationAuthenticationException(exception)
            )
        }
    }
}