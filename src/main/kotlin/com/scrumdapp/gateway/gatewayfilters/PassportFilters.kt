package com.scrumdapp.gateway.gatewayfilters

import com.scrumdapp.gateway.services.JwtService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.time.Instant

data class JwtToken(
    val token: String,
    val expiresAt: Instant
) {
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}

@Component
class PassportFilters(
    private val jwtService: JwtService
) {

    fun insertPassport(): HandlerFilterFunction<ServerResponse, ServerResponse> {

        return HandlerFilterFunction { req: ServerRequest, next: HandlerFunction<ServerResponse> ->
            val session = req.session()
            var cachedToken = session.getAttribute("JWT_AC_TOKEN") as? JwtToken

            if (cachedToken == null || cachedToken.isExpired()) {
                cachedToken = generateNewPassport(session)
                session.setAttribute("JWT_AC_TOKEN", cachedToken)
            }

            val mutatedReq = ServerRequest.from(req)
                .headers {
                    // Session has been validated at this point in the chain
                    it.remove(HttpHeaders.COOKIE)
                    it.setBearerAuth(cachedToken.token)
                }
                .build()

            val response = next.handle(mutatedReq)
            response
        }
    }

    private fun generateNewPassport(session: HttpSession): JwtToken {
        val expiresAt = Instant.now().plusSeconds(60*5)

        val token = jwtService.generateJwtToken(
            subject = "name",
            claims = mapOf("userId" to 24, "user_groups" to 1, "roles" to listOf("STUDENT")) //TODO( Change this some real logic )
        )

        return JwtToken(token, expiresAt)
    }
}
