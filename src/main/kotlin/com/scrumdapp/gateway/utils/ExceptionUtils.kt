package com.scrumdapp.gateway.utils

import com.scrumdapp.gateway.handlers.exceptions.ApiError
import com.scrumdapp.gateway.handlers.exceptions.ApplicationException
import org.springframework.stereotype.Component

interface ExceptionUtils {
    fun exceptionFromThrowable(throwable: Throwable): ApiError
    fun Throwable.toExceptionContent(): ApiError
}

@Component
class ExceptionUtilsImpl: ExceptionUtils {
    override fun exceptionFromThrowable(throwable: Throwable): ApiError {
        return when (throwable) {
            is ApplicationException -> ApiError(
                code = throwable.code,
                message = throwable.message,
                stackTrace = throwable.stackTraceToString()
            )
            else -> ApiError(
                code = 500,
                message = throwable.message ?: "Unknown server error",
                stackTrace = throwable.stackTraceToString()
            )
        }
    }

    override fun Throwable.toExceptionContent(): ApiError {
        return exceptionFromThrowable(this)
    }
}