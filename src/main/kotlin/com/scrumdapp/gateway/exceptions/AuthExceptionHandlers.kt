package com.scrumdapp.gateway.exceptions

import com.scrumdapp.gateway.services.ExceptionService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
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
    private val exceptionService: ExceptionService
): AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val body = exceptionService.mapException(exception.cause)
        exceptionService.returnException(response, body)
    }
}

@Component
class CustomAuthenticationEntrypoint(
    private val exceptionService: ExceptionService
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
        exceptionService.returnException(response, body)
    }
}

@Component
class CustomAccessDeniedHandler(
    private val exceptionService: ExceptionService
): AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val body = exceptionService.mapException(accessDeniedException.cause)
        exceptionService.returnException(response, body)
    }
}

@RestControllerAdvice
class ControllerExceptionHandler(
    private val exceptionService: ExceptionService
) {
    @ExceptionHandler(ApplicationException::class)
    fun andleApplicationException(e: ApplicationException, res: HttpServletResponse) {
        val body = exceptionService.mapException(e)
        exceptionService.returnException(res, body)
    }
}