/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.repository.TestResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveTestResult(
    private val testResultRepository: TestResultRepository,
    private val getUserDetails: GetUserDetails
) : Interactor<SaveTestResult.Params, Unit>() {

    override suspend fun doWork(params: Params): Unit = withContext(Dispatchers.IO) {
        val userDetails = getUserDetails.executeSync(Unit)

        testResultRepository.saveTestResult(
            testResult = params.testResult.copy(userDetails = userDetails)
        )
    }

    data class Params(val testResult: TestResult)
}