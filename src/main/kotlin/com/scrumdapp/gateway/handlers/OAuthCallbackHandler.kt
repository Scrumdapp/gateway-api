package com.scrumdapp.gateway.handlers

import com.scrumdapp.gateway.handlers.exceptions.NoAccessException
import com.scrumdapp.gateway.handlers.exceptions.NotAuthorizedException
import com.scrumdapp.gateway.handlers.exceptions.ServerFaultException
import com.scrumdapp.gateway.services.DiscordService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Component
class OAuthCallbackHandler(
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val discordService: DiscordService,
): AuthenticationSuccessHandler  {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = authentication as? OAuth2AuthenticationToken

        if (token != null && "discord".equals(token.authorizedClientRegistrationId, ignoreCase = true)) {
            val client: OAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
                token.authorizedClientRegistrationId,
                token.name
            )
            //TODO(Add error handling here)
            if (client.refreshToken == null) throw NotAuthorizedException()

            val accessToken = client.accessToken.tokenValue

            val s = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            s.timeZone = TimeZone.getTimeZone("GMT")
            //TODO(Add expiry time here)
            val tokenExpiry = s.format(Date()) + client.refreshToken?.expiresAt

            val discordUser = discordService.getUser(accessToken).getOrElse {
                throw ServerFaultException(message = "Could not fetch user from Discord")
            }

            //TODO(Add env for authorized guild)
            val result = discordService.isInServer(accessToken, "123")
            if (!result) {
                throw NoAccessException(message = "Your Discord account is not authorized to access this resource")
            }

            // Update shit within user db

            println(discordUser.username)
        }


        //where ever you want
        response.sendRedirect("/api/groups")
    }
}