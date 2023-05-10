/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlada.quiz.data.dao.TestDao
import com.vlada.quiz.data.model.FirestorePagedResult
import com.vlada.quiz.data.model.TestDto
import com.vlada.quiz.domain.model.TestsSortOption
import kotlinx.coroutines.tasks.await


class FirebaseTestDao : TestDao {
    private val firestore = Firebase.firestore

    override suspend fun saveTest(test: TestDto) {
        val updatedTest = test.copy(
            searchTitle = test.title.lowercase(),
            timestamp = if (test.id == null) {
                System.currentTimeMillis()
            } else {
                test.timestamp
            }
        )

        val collectionRef = firestore.collection("tests")

        if (test.id == null) {
            collectionRef.add(updatedTest)
                .await()
        } else {
            collectionRef.document(test.id)
                .set(updatedTest).await()
        }
    }

    override suspend fun deleteTest(testId: String) {
        firestore.collection("tests")
            .document(testId)
            .delete()
            .await()
    }

    override suspend fun getTest(testId: String): TestDto? {
        return firestore.collection("tests")
            .document(testId)
            .get()
            .await()
            .toObject()
    }

    override suspend fun getTests(
        pageSize: Int,
        sort: TestsSortOption,
        searchQuery: String,
        teacherId: String?,
        lastDocument: DocumentSnapshot?
    ): FirestorePagedResult<List<TestDto>> {
        val sortDirection = when (sort) {
            TestsSortOption.ASCENDING -> {
                Query.Direction.ASCENDING
            }

            TestsSortOption.DESCENDING -> {
                Query.Direction.DESCENDING
            }
        }

        val result = firestore.collection("tests").run {
            if (teacherId != null) {
                whereEqualTo("teacherId", teacherId)
            } else this
        }.run {
            if (searchQuery.isNotBlank()) {
                val lowercaseSearchQuery = searchQuery.lowercase()

                orderBy("searchTitle", Query.Direction.DESCENDING)
                    .whereGreaterThanOrEqualTo(
                        "searchTitle",
                        lowercaseSearchQuery
                    )
                    .whereLessThanOrEqualTo(
                        "searchTitle",
                        lowercaseSearchQuery + '\uF7FF'
                    )
            } else {
                orderBy("timestamp", sortDirection)
            }
        }.run {
            if (lastDocument != null) {
                startAfter(lastDocument)
            } else this
        }.limit(pageSize.toLong())
            .get()
            .await()

        val tests = result.toObjects<TestDto>()

        val lastRetrievedDocument = result.documents
            .takeIf { it.size == pageSize }?.lastOrNull()

        return FirestorePagedResult(
            data = tests,
            lastDocument = lastRetrievedDocument
        )
    }
}