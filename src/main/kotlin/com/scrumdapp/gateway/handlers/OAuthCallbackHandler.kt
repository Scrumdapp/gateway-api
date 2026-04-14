package com.scrumdapp.gateway.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import com.scrumdapp.gateway.handlers.exceptions.ApplicationAuthenticationException
import com.scrumdapp.gateway.handlers.exceptions.ApplicationException
import com.scrumdapp.gateway.handlers.exceptions.NoAccessException
import com.scrumdapp.gateway.handlers.exceptions.NotAuthorizedException
import com.scrumdapp.gateway.handlers.exceptions.ServerFaultException
import com.scrumdapp.gateway.services.DiscordService
import com.scrumdapp.gateway.services.JwtService
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Component
class OAuthCallbackHandler(
    private val failureHandler: AuthenticationFailureHandler,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val jwtService: JwtService,
    private val discordService: DiscordService,
    @Value($$"${DISCORD_ALLOWED_GUILD}") private val allowedGuild: String
): AuthenticationSuccessHandler  {

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

                if (client.refreshToken == null) throw NotAuthorizedException()

                val accessToken = client.accessToken.tokenValue

                val s = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                s.timeZone = TimeZone.getTimeZone("GMT")

                //TODO(Add expiry time here)
                val tokenExpiry = s.format(Date()) + client.refreshToken?.expiresAt

                val discordUser = discordService.getUser(accessToken).getOrElse {
                    throw ServerFaultException(message = "Could not fetch user from Discord")
                }

                val result = discordService.isInServer(accessToken, allowedGuild)
                if (!result) {
                    throw NoAccessException(message = "Your Discord account is not authorized to access this resource")
                }

                //TODO( Update shit within user db )

                response.status = HttpStatus.OK.value()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, ApiResponse(code = 200, "login successful"))
            } else {

                //TODO(error not thrown properly here
                throw NoAccessException(message = "Your Discord account is not authorized to access this resource")
            }
        } catch (exception: ApplicationException) {

            if (exception is NoAccessException) {
                println("Reached this")
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
