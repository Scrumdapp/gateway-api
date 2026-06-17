package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.exceptions.NoAccessException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

@Component
class GatewayUtilityFilters {

    fun blockActuatorRequests(): HandlerFilterFunction<ServerResponse, ServerResponse> {
        return HandlerFilterFunction { req: ServerRequest, next: HandlerFunction<ServerResponse> ->

            val path = req.path()
            if (path.contains("actuator/", ignoreCase = true)) {
                throw NoAccessException()
            }

            next.handle(req)
        }
    }
}