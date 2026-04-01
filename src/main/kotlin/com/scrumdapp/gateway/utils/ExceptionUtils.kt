package com.scrumdapp.gateway.utils

import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import com.scrumdapp.gateway.handlers.exceptions.ApplicationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface ExceptionUtils {
    fun exceptionFromThrowable(throwable: Throwable): ApiResponse
    fun Throwable.toExceptionContent(): ApiResponse
    fun logException(throwable: Throwable)
}

@Component
class ExceptionUtilsImpl(
    private val logger: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
): ExceptionUtils {
    override fun exceptionFromThrowable(throwable: Throwable): ApiResponse {
        return when (throwable) {
            is ApplicationException -> ApiResponse(
                code = throwable.code,
                message = throwable.message,
                stackTrace = throwable.stackTraceToString()
            )
            else -> ApiResponse(
                code = 500,
                message = throwable.message ?: "Unknown server error",
                stackTrace = throwable.stackTraceToString()
            )
        }
    }

    override fun Throwable.toExceptionContent(): ApiResponse {
        return exceptionFromThrowable(this)
    }

    override fun logException(throwable: Throwable)
    {
        if (throwable is ApplicationException && throwable.enableLogging) {
            logger.error("${throwable.code} - ${throwable.message}", throwable.stackTrace)
        } else {
            logger.error(throwable.message, throwable.stackTrace)
        }

    }
}