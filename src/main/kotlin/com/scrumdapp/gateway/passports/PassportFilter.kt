package com.scrumdapp.gateway.passports

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

@Component
class PassportFilters(
    private val passportService: PassportService
) {

    fun insertPassport(): HandlerFilterFunction<ServerResponse, ServerResponse> {

        return HandlerFilterFunction { req: ServerRequest, next: HandlerFunction<ServerResponse> ->
            val session = req.session() ?: return@HandlerFilterFunction next.handle(req)

            var cachedToken = session.getAttribute("JWT_AC_TOKEN") as? PassportToken

            if (cachedToken == null || cachedToken.isExpired()) {
                cachedToken = passportService.generatePassport(23)
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
}
