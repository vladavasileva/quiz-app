/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao

import com.google.firebase.firestore.DocumentSnapshot
import com.vlada.quiz.data.model.FirestorePagedResult
import com.vlada.quiz.data.model.TestDto
import com.vlada.quiz.domain.model.TestsSortOption

interface TestDao {
    suspend fun saveTest(test: TestDto)

    suspend fun deleteTest(testId: String)

    suspend fun getTest(testId: String): TestDto?

    suspend fun getTests(
        pageSize: Int,
        sort: TestsSortOption,
        searchQuery: String,
        teacherId: String?,
        lastDocument: DocumentSnapshot?
    ): FirestorePagedResult<List<TestDto>>
}