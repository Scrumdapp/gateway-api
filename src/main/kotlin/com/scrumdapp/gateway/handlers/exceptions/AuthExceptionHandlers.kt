package com.scrumdapp.gateway.handlers.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumdapp.gateway.utils.ExceptionUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationFailureHandler(
    private val exceptionUtils: ExceptionUtils,
): AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val throwable = exception.cause
        var body: ApiResponse
        if (throwable != null) {
            exceptionUtils.logException(throwable)
            if (throwable is ApplicationException) {

                response.status = throwable.code
                body = exceptionUtils.exceptionFromThrowable(throwable)
            } else {
                response.status = HttpStatus.UNAUTHORIZED.value()
                body = ApiResponse(
                    code = HttpStatus.UNAUTHORIZED.value(),
                    message = "The resource owner of the auth server denied the request"
                )
            }
            ObjectMapper().writeValue(response.outputStream, body)
        }
    }
}

@Component
class CustomAuthenticationEntrypoint: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val body = ApiResponse(
            code = HttpStatus.UNAUTHORIZED.value(),
            message = "Not authorized, please log in"
        )

        response.status = body.code
        ObjectMapper().writeValue(response.outputStream, body)
    }
}

@Component
class CustomAccessDeniedHandler(
    private val exceptionUtils: ExceptionUtils,
): AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        var body: ApiResponse
        val throwable = accessDeniedException.cause

        if (throwable != null) {
            exceptionUtils.logException(throwable)

            if (throwable is ApplicationException) {
                response.status = throwable.code
                body = ApiResponse(
                    throwable.code,
                    throwable.message,
                )
            } else {
                response.status = HttpStatus.FORBIDDEN.value()
                body = ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    accessDeniedException.message?: "Unknown error",
                )
            }
            ObjectMapper().writeValue(response.outputStream, body)
        }
    }
}