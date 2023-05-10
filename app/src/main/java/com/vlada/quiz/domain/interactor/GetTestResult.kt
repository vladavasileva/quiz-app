/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.repository.TestResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTestResult(
    private val testResultRepository: TestResultRepository,
    private val getUserDetails: GetUserDetails
) : Interactor<GetTestResult.Params, TestResult>() {

    override suspend fun doWork(params: Params): TestResult = withContext(Dispatchers.IO) {
        val userId = getUserDetails.executeSync(Unit).userId ?: throw UnexpectedException()

        return@withContext testResultRepository.getTestResult(
            testId = params.testId,
            userId = userId
        ) ?: TestResult.Empty
    }

    data class Params(val testId: String)
}