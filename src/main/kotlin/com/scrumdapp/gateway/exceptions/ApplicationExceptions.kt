package com.scrumdapp.gateway.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException

class NotAuthorizedException(
    override val code: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val message: String = "Not authorized",
): ApplicationException(code, message)

class NoAccessException(
    override val code: HttpStatus = HttpStatus.FORBIDDEN,
    override val message: String = "Access denied",
): ApplicationException(code, message)

class NotFoundException(
    override val code: HttpStatus = HttpStatus.NOT_FOUND,
    override val message: String = "Resource not found",
): ApplicationException(code, message)

class ServerFaultException(
    override val code: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    override val message: String = "Unexpected server error",
    override val enableLogging: Boolean = true
): ApplicationException(code, message, enableLogging)

class ServiceUnavailableException(
    override val code: HttpStatus = HttpStatus.SERVICE_UNAVAILABLE,
    override val message: String = "Service not available",
    override val enableLogging: Boolean = true
): ApplicationException(code, message, enableLogging)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse(val code: Int, val message: String, val stackTrace: String? = null)

open class ApplicationException(
    open val code: HttpStatus,
    override val message: String,
    open val enableLogging: Boolean = false
): RuntimeException()

class ApplicationAuthenticationException(
    appException: ApplicationException
): AuthenticationException(appException.message)