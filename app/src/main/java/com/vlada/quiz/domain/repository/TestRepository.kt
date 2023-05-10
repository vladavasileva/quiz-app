/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.repository

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestsSortOption

interface TestRepository {
    suspend fun saveTest(test: Test)

    suspend fun deleteTest(testId: String)

    suspend fun getTest(testId: String): Test?

    fun getTestsPagingSource(
        sort: TestsSortOption,
        searchQuery: String,
        teacherId: String?
    ): PagingSource<DocumentSnapshot, Test>
}