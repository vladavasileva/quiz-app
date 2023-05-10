/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vlada.quiz.domain.exception.AppException
import com.vlada.quiz.domain.exception.UnexpectedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

abstract class Interactor<in P, out R> {
    companion object {
        private val defaultTimeoutMs = 30.seconds.inWholeMilliseconds
    }

    operator fun invoke(
        params: P,
        timeoutMs: Long = defaultTimeoutMs
    ): Flow<InvokeStatus<out R>> = flow {
        try {
            withTimeout(timeoutMs) {
                emit(InvokeStatus.InvokeStarted)
                emit(InvokeStatus.InvokeSuccess(result = doWork(params)))
            }
        } catch (e: TimeoutCancellationException) {
            emit(InvokeStatus.InvokeError(UnexpectedException()))
        }
    }.catch { t ->
        if(t is AppException) {
            emit(InvokeStatus.InvokeError(t))
        }
    }

    suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R
}

abstract class SubjectInteractor<P : Any, T> {
    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow: Flow<T> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
        .distinctUntilChanged()

    operator fun invoke(params: P) {
        paramState.tryEmit(params)
    }

    protected abstract fun createObservable(params: P): Flow<T>
}

abstract class PagingInteractor<P : PagingInteractor.Params<T>, T : Any> : SubjectInteractor<P, PagingData<T>>() {
    interface Params<T : Any> {
        val pagingConfig: PagingConfig
    }
}