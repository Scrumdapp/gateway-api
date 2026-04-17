package com.scrumdapp.gateway.handlers.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumdapp.gateway.utils.ExceptionResponseMapper
import com.scrumdapp.gateway.utils.ExceptionResponseWriter
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
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Component
class CustomAuthenticationFailureHandler(
    private val exceptionMapper: ExceptionResponseMapper,
    private val exceptionWriter: ExceptionResponseWriter
): AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val body = exceptionMapper.map(exception.cause)
        exceptionWriter.write(response, body)
    }
}

@Component
class CustomAuthenticationEntrypoint(
    private val exceptionWriter: ExceptionResponseWriter
): AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val body = ApiResponse(
            code = HttpStatus.UNAUTHORIZED.value(),
            message = "Not authorized, please log in"
        )
        exceptionWriter.write(response, body)
    }
}

@Component
class CustomAccessDeniedHandler(
    private val exceptionMapper: ExceptionResponseMapper,
    private val exceptionWriter: ExceptionResponseWriter
): AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val body = exceptionMapper.map(accessDeniedException.cause)
        exceptionWriter.write(response, body)
    }
}

@RestControllerAdvice
class ControllerExceptionHandler(
    private val exceptionMapper: ExceptionResponseMapper,
    private val exceptionWriter: ExceptionResponseWriter
) {
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(e: ApplicationException, res: HttpServletResponse) {
        val body = exceptionMapper.map(e)
        exceptionWriter.write(res, body)
    }
}