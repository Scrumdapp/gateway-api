package com.scrumdapp.gateway.utils

import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import com.scrumdapp.gateway.handlers.exceptions.ApplicationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpServerErrorException

@Component
class ExceptionResponseMapper(
    private val exceptionUtils: ExceptionUtils
) {
    fun map(throwable: Throwable?): ApiResponse {
        if (throwable == null) {
            return ApiResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
            )
        }

        exceptionUtils.logException(throwable)

        return when (throwable) {
            is ApplicationException -> {
                ApiResponse(
                    throwable.code.value(),
                    throwable.message,
                )
            }
            is HttpServerErrorException -> {
                handleDownStreamError(throwable)
            }
            else -> {
                ApiResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                )
            }
        }
    }

    private fun handleDownStreamError(e: HttpServerErrorException): ApiResponse {

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