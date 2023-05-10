/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.data.dao.TestResultDao
import com.vlada.quiz.data.mapper.toDomainModel
import com.vlada.quiz.data.mapper.toDto
import com.vlada.quiz.domain.exception.FailedToSaveTheTestException
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.repository.TestResultRepository
import com.vlada.quiz.domain.repository.UserDetailsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


class TestResultRepositoryImpl(
    private val testResultDao: TestResultDao,
    private val userDetailsRepository: UserDetailsRepository
) : TestResultRepository {

    private val TAG = TestResultRepositoryImpl::class.java.simpleName

    override suspend fun saveTestResult(testResult: TestResult) {
        try {
            testResultDao.saveTestResult(testResult = testResult.toDto())
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw FailedToSaveTheTestException()
        }
    }

    override suspend fun getTestResult(testId: String, userId: String): TestResult? {
        return try {
            val userDetails = userDetailsRepository.getUserDetails(
                userId = userId
            ) ?: return null

            testResultDao.getTestResult(
                testId = testId,
                userId = userId
            )?.toDomainModel(userDetails = userDetails)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override fun getTestResultsPagingSource(
        testId: String
    ) = object : PagingSource<DocumentSnapshot, TestResult>() {

        override fun getRefreshKey(
            state: PagingState<DocumentSnapshot, TestResult>
        ): DocumentSnapshot? {
            return null
        }

        override suspend fun load(
            params: LoadParams<DocumentSnapshot>
        ): LoadResult<DocumentSnapshot, TestResult> = coroutineScope {
            return@coroutineScope try {
                val pageSize = params.loadSize

                val response = testResultDao.getTestResults(
                    pageSize = pageSize,
                    testId = testId,
                    lastDocument = params.key
                )

                val testResults = response.data.mapNotNull { testResult ->
                    testResult.userId?.let { userId ->
                        async {
                            val userDetails = userDetailsRepository.getUserDetails(
                                userId = userId
                            ) ?: return@async null

                            testResult.toDomainModel(userDetails = userDetails)
                        }
                    }
                }.awaitAll().filterNotNull()

                LoadResult.Page(
                    data = testResults,
                    prevKey = null,
                    nextKey = response.lastDocument,
                )
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
                LoadResult.Error(UnexpectedException())
            }
        }
    }
}