package com.scrumdapp.gateway.exceptions

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.webmvc.error.ErrorController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExceptionController: ErrorController {

    @GetMapping("/error")
    fun error(request: HttpServletRequest) {
        val statusCode = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString())

        when (statusCode) {
            401 -> throw NotAuthorizedException()
            403 -> throw NoAccessException()
            404 -> throw NotFoundException()
            500 -> throw ServerFaultException()
            503 -> throw ServiceUnavailableException()
            else -> throw ServerFaultException()
        }
    }
}