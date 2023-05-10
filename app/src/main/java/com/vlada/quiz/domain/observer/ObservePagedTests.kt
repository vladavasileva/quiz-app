/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.observer

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vlada.quiz.domain.PagingInteractor
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestsSortOption
import com.vlada.quiz.domain.repository.TestRepository
import com.vlada.quiz.domain.repository.UserAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class ObservePagedTests(
    private val testRepository: TestRepository,
    private val userAuthRepository: UserAuthRepository
) : PagingInteractor<ObservePagedTests.Params, Test>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createObservable(
        params: Params,
    ): Flow<PagingData<Test>> = flow {
        val teacherId = params.teacher?.let {
            when (params.teacher) {
                "me" -> userAuthRepository.getUserId()
                else -> params.teacher
            }
        }

        emit(teacherId)
    }.flatMapLatest { teacherId ->
        Pager(config = params.pagingConfig) {
            testRepository.getTestsPagingSource(
                sort = params.sort,
                searchQuery = params.searchQuery,
                teacherId = teacherId
            )
        }.flow
    }

    data class Params(
        val sort: TestsSortOption,
        val searchQuery: String,
        val teacher: String?,
        override val pagingConfig: PagingConfig
    ) : PagingInteractor.Params<Test>
}