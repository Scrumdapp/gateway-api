package com.scrumdapp.gateway.handlers.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.security.core.AuthenticationException

class NotAuthorizedException(
    override val code: Int = 401,
    override val message: String = "Not authorized",
): ApplicationException(code, message)

class NoAccessException(
    override val code: Int = 403,
    override val message: String = "Access denied",
): ApplicationException(code, message)

class NotFoundException(
    override val code: Int = 404,
    override val message: String = "Resource not found",
): ApplicationException(code, message)

class ServerFaultException(
    override val code: Int = 500,
    override val message: String = "Unexpected server error",
    override val enableLogging: Boolean = true
): ApplicationException(code, message, enableLogging)

class ServiceUnavailableException(
    override val code: Int = 503,
    override val message: String = "Service not available",
    override val enableLogging: Boolean = true
): ApplicationException(code, message, enableLogging)

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