/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTest(
    private val testRepository: TestRepository
) : Interactor<DeleteTest.Params, Unit>() {

    override suspend fun doWork(params: Params) = withContext(Dispatchers.IO) {
        testRepository.deleteTest(testId = params.testId)
    }

    data class Params(val testId: String)
}