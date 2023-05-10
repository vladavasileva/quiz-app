/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTest(
    private val testRepository: TestRepository
) : Interactor<GetTest.Params, Test>() {

    override suspend fun doWork(params: Params): Test = withContext(Dispatchers.IO) {
        return@withContext testRepository.getTest(testId = params.testId) ?: Test.Empty
    }

    data class Params(val testId: String)
}