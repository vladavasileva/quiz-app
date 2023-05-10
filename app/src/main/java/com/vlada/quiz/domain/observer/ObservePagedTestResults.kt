/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.observer

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vlada.quiz.domain.PagingInteractor
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.repository.TestResultRepository
import kotlinx.coroutines.flow.Flow

class ObservePagedTestResults(
    private val testResultRepository: TestResultRepository
) : PagingInteractor<ObservePagedTestResults.Params, TestResult>() {

    override fun createObservable(
        params: Params,
    ): Flow<PagingData<TestResult>> = Pager(config = params.pagingConfig) {
        testResultRepository.getTestResultsPagingSource(
            testId = params.testId
        )
    }.flow

    data class Params(
        val testId: String,
        override val pagingConfig: PagingConfig
    ) : PagingInteractor.Params<TestResult>
}