package com.scrumdapp.gateway.exceptions

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import tools.jackson.databind.ObjectMapper


@Service
class ExceptionService(
    private val objectMapper: ObjectMapper,
    private val logger: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
) {

    fun logException(throwable: Throwable)
    {
        if (throwable is ApplicationException && throwable.enableLogging) {
            logger.error("${throwable.code} - ${throwable.message}", throwable)
        } else {
            logger.error(throwable.message, throwable)
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