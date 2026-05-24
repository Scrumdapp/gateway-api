package com.scrumdapp.gateway.exceptions

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientResponseException
import tools.jackson.databind.ObjectMapper


@Service
class ExceptionService(
    private val objectMapper: ObjectMapper,
    private val logger: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
) {

    fun logException(ex: Throwable)
    {
        when (ex) {
            is ApplicationException ->
                if (ex.enableLogging) {
                    logger.warn(
                        "${ex.code}: ${ex.message}",
                        ex
                    )
                }

            is RestClientResponseException ->
                logger.error(
                    "Downstream service returned ${ex.statusCode}",
                    ex
                )

            is ResourceAccessException ->
                logger.error(
                    "Downstream service unavailable",
                    ex
                )

            else ->
                logger.error(
                    "Unhandled exception",
                    ex
                )
        }
    }

    fun returnException(
        res: HttpServletResponse,
        body: ApiResponse
    ) {
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

       logException(throwable)

        return when (throwable) {
            is ApplicationException -> {
                ApiResponse(
                    throwable.code.value(),
                    throwable.message,
                )
            }
            is HttpServerErrorException -> {
                mapDownstreamErrors(throwable)
            }
            else -> {
                ApiResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                )
            }
        }
    }

    private fun mapDownstreamErrors(e: HttpServerErrorException): ApiResponse {
        return when (e.statusCode) {
            HttpStatus.SERVICE_UNAVAILABLE -> {
                ApiResponse(
                    503,
                    "Service currently unavailable",
                )
            }
            else -> {
                ApiResponse(
                    e.statusCode.value(),
                    "Service error"
                )
            }
        }
    }
}