package com.scrumdapp.gateway.handlers.exceptions

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
    override val message: String = "Not found",
): ApplicationException(code, message)

class ServerFaultException(
    override val code: Int = 500,
    override val message: String = "Unexpected server error",
    override val enableLogging: Boolean = true
): ApplicationException(code, message, enableLogging)