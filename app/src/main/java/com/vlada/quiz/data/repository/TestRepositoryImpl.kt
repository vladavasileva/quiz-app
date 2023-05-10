/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.data.dao.TestDao
import com.vlada.quiz.data.mapper.toDomainModel
import com.vlada.quiz.data.mapper.toDto
import com.vlada.quiz.domain.exception.FailedToSaveTheTestException
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestsSortOption
import com.vlada.quiz.domain.repository.TestRepository


class TestRepositoryImpl(
    private val testDao: TestDao
) : TestRepository {

    private val TAG = TestRepositoryImpl::class.java.simpleName

    override suspend fun saveTest(test: Test) {
        try {
            testDao.saveTest(test = test.toDto())
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw FailedToSaveTheTestException()
        }
    }

    override suspend fun deleteTest(testId: String) {
        try {
            testDao.deleteTest(testId = testId)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override suspend fun getTest(testId: String): Test? {
        return try {
            testDao.getTest(testId = testId)?.toDomainModel()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override fun getTestsPagingSource(
        sort: TestsSortOption,
        searchQuery: String,
        teacherId: String?
    ) = object : PagingSource<DocumentSnapshot, Test>() {

        override fun getRefreshKey(state: PagingState<DocumentSnapshot, Test>): DocumentSnapshot? {
            return null
        }

        override suspend fun load(
            params: LoadParams<DocumentSnapshot>
        ): LoadResult<DocumentSnapshot, Test> {
            return try {
                val pageSize = params.loadSize

                val response = testDao.getTests(
                    pageSize = pageSize,
                    sort = sort,
                    searchQuery = searchQuery,
                    teacherId = teacherId,
                    lastDocument = params.key
                )

                LoadResult.Page(
                    data = response.data.map { it.toDomainModel() },
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