/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.repository

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.domain.model.TestResult

interface TestResultRepository {
    suspend fun saveTestResult(testResult: TestResult)

    suspend fun getTestResult(
        testId: String,
        userId: String
    ): TestResult?

    fun getTestResultsPagingSource(
        testId: String
    ): PagingSource<DocumentSnapshot, TestResult>
}