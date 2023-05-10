/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.util

import com.vlada.quiz.domain.InvokeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

fun <R> Flow<InvokeStatus<out R>>.withLoader(
    loadingCounter: ObservableLoadingCounter
) = onEach { status ->
    when (status) {
        InvokeStatus.InvokeStarted -> {
            loadingCounter.addLoader()
        }
        is InvokeStatus.InvokeSuccess -> {
            loadingCounter.removeLoader()
        }
        is InvokeStatus.InvokeError -> {
            loadingCounter.removeLoader()
        }
    }
}

fun <R> Flow<InvokeStatus<out R>>.withErrorMessage(
    uiMessageManager: UiMessageManager
) = onEach { status ->
    if (status is InvokeStatus.InvokeError) {
        uiMessageManager.emitMessage(
            UiMessage(
                resId = status.exception.messageResId,
                message = status.exception.message
            )
        )
    }
}

fun <R> Flow<InvokeStatus<out R>>.onSuccess(
    onSuccess: (R) -> Unit
) = onEach { status ->
    if(status is InvokeStatus.InvokeSuccess) {
        onSuccess(status.result)
    }
}

fun <R> Flow<InvokeStatus<out R>>.onError(
    onError: (Exception) -> Unit
) = onEach { status ->
    if(status is InvokeStatus.InvokeError) {
        onError(status.exception)
    }
}