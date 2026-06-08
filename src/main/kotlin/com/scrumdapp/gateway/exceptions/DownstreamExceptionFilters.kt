package com.scrumdapp.gateway.exceptions

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

@Component
class DownstreamExceptionFilters {


    fun handleClientErrors(): HandlerFilterFunction<ServerResponse, ServerResponse> {

        return HandlerFilterFunction { req: ServerRequest, next: HandlerFunction<ServerResponse> ->

            val res = next.handle(req)
            res
        }
    }
}

