package com.scrumdapp.gateway.handlers.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumdapp.gateway.utils.ExceptionUtils
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse(val code: Int, val message: String, val stackTrace: String? = null)

open class ApplicationException(
    open val code: Int,
    override val message: String,
    open val enableLogging: Boolean = false
): RuntimeException()

class ApplicationAuthenticationException(
    appException: ApplicationException
): AuthenticationException(appException.message)

@Component
class RuntimeExceptionHandler(
    private val exceptionUtils: ExceptionUtils,
    private val logger: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
): AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val throwable = exception.cause
        var body: ApiResponse
        println(exception.message)
        if (throwable != null && throwable is ApplicationException) {
            if (throwable.enableLogging) {
                logger.error(throwable.message, throwable.message, throwable.stackTrace)
            }

            response.status = throwable.code
            body = exceptionUtils.exceptionFromThrowable(throwable)
        } else {
            response.status = HttpStatus.FORBIDDEN.value()
            body = ApiResponse(
                code = HttpStatus.FORBIDDEN.value(),
                message = exception.message?: "Forbidden"
            )
        }

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, body)
    }
}

@Component
class CustomAuthenticationEntryPoint: AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    )  {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        var apiError: ApiResponse
        val throwable = authException.cause
        if (throwable != null && throwable is ApplicationException) {
            response.status = throwable.code
            apiError = ApiResponse(
                throwable.code,
                throwable.message,
            )
        } else {
            response.status = HttpStatus.FORBIDDEN.value()
            apiError = ApiResponse(
                HttpStatus.FORBIDDEN.value(),
                authException.message?: "Unknown error",
            )
        }

        ObjectMapper().writeValue(response.outputStream, apiError)
    }

}