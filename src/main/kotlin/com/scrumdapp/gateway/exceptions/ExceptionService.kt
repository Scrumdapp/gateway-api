package com.scrumdapp.gateway.exceptions

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.servlet.resource.NoResourceFoundException
import tools.jackson.databind.ObjectMapper
import java.net.ConnectException


@Service
class ExceptionService(
    private val objectMapper: ObjectMapper,
    private val logger: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
) {

    fun logException(throwable: Throwable, request: HttpServletRequest? = null) {

        val ex = throwable.cause ?: throwable
        val path = request?.requestURI

        when (ex) {
            is ApplicationException ->
                if (ex.enableLogging) {
                    logger.info("${ex.code}: ${ex.message}", path, ex)
                }

            is AccessDeniedException, is AuthenticationException ->
                logger.info("[{}] Auth failure: {}", path, ex.message)

            is NoResourceFoundException ->
                return

            is HttpServerErrorException ->
                logger.warn("[{}] Downstream service returned ${ex.statusCode}: ${ex.responseBodyAsString}", path, ex)

            is ResourceAccessException ->
                logger.warn("[{}] Downstream service unavailable {}", path, ex.message)

            is ConnectException ->
                logger.warn("[{}] Could not connect to downstream service {}", path, ex.message)

            else ->
                logger.error("[{}] Unhandled exception", path, ex)
        }
    }

    fun returnException(
        res: HttpServletResponse,
        body: ApiResponse
    ) {
        if (res.isCommitted) {
            logger.warn("Response already committed! Cannot write body")
            return
        }

        res.status = body.code
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(res.outputStream, body)
    }

    fun mapException(throwable: Throwable?): ApiResponse {
        if (throwable == null) {
            return ApiResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
            )
        }

        val (code, message) = when (throwable) {
            is ApplicationException ->
                throwable.code.value() to (throwable.message ?: "Application Error")

            is AccessDeniedException ->
                HttpStatus.FORBIDDEN.value() to "Access Denied"

            is AuthenticationException ->
                HttpStatus.UNAUTHORIZED.value() to "Not Authorized, please log in"

            is NoResourceFoundException ->
                HttpStatus.NOT_FOUND.value() to "Resource not found"

            is HttpServerErrorException ->
                mapDownstreamErrors(throwable)

            is ResourceAccessException ->
                HttpStatus.SERVICE_UNAVAILABLE.value() to "Downstream service unavailable"

            else -> {
                println("Uncaught Throwable: $throwable")
                HttpStatus.INTERNAL_SERVER_ERROR.value() to "Unknown error"
            }
        }

        return ApiResponse(code, message)
    }

    private fun mapDownstreamErrors(e: HttpServerErrorException): Pair<Int, String> {
        return when (e.statusCode) {
            HttpStatus.SERVICE_UNAVAILABLE ->
                HttpStatus.SERVICE_UNAVAILABLE.value() to "Downstream service currently unavailable"
            else -> e.statusCode.value() to e.message!!
        }
    }
}