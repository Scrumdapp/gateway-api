package com.scrumdapp.gateway.controllers

import com.scrumdapp.gateway.handlers.exceptions.NoAccessException
import com.scrumdapp.gateway.handlers.exceptions.NotAuthorizedException
import com.scrumdapp.gateway.handlers.exceptions.NotFoundException
import com.scrumdapp.gateway.handlers.exceptions.ServerFaultException
import com.scrumdapp.gateway.handlers.exceptions.ServiceUnavailableException
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