package com.scrumdapp.gateway.passports

import com.scrumdapp.gateway.exceptions.NotAuthorizedException
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

            val userId = session.getAttribute("userId") as? Long
                ?: throw NotAuthorizedException(message = "Not authorized, please log in")

            val token = passportService.getPassportToken(userId)

            val mutatedReq = ServerRequest.from(req)
                .headers {
                    // Session has been validated at this point in the chain
                    it.remove(HttpHeaders.COOKIE)
                    it.setBearerAuth(token)
                }
                .build()

            val response = next.handle(mutatedReq)
            response
        }
    }
}
