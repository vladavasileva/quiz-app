/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain

import com.vlada.quiz.domain.exception.AppException

sealed class InvokeStatus<R> {
    object InvokeStarted : InvokeStatus<Nothing>()
    class InvokeSuccess<R>(val result: R) : InvokeStatus<R>()
    class InvokeError(val exception: AppException) : InvokeStatus<Nothing>()
}