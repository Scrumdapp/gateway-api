package com.scrumdapp.gateway.gatewayfilters

import org.springframework.http.HttpMethod
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse


class PostUpstreamFilter(
    private val passportRevokePaths: Map<String, List<HttpMethod>>
) {

    fun invalidatePassport(): HandlerFilterFunction<ServerResponse, ServerResponse> {

        return HandlerFilterFunction { req: ServerRequest, next: HandlerFunction<ServerResponse> ->

            val res = next.handle(req)
            val session = req.session() ?: return@HandlerFilterFunction res

            if (passportRevokePaths.containsKey(req.path()) && passportRevokePaths[req.path()]?.contains(req.method()) == true) {
                session.removeAttribute("JWT_AC_TOKEN")
            }

            res
        }
    }
}