/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao

import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.data.model.FirestorePagedResult
import com.vlada.quiz.data.model.TestResultDto

interface TestResultDao {
    suspend fun saveTestResult(testResult: TestResultDto)

    suspend fun getTestResult(
        testId: String,
        userId: String
    ): TestResultDto?

    suspend fun getTestResults(
        pageSize: Int,
        testId: String,
        lastDocument: DocumentSnapshot?
    ): FirestorePagedResult<List<TestResultDto>>
}