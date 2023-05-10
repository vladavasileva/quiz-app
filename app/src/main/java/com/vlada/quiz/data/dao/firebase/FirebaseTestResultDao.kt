/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlada.quiz.data.dao.TestResultDao
import com.vlada.quiz.data.model.FirestorePagedResult
import com.vlada.quiz.data.model.TestResultDto
import kotlinx.coroutines.tasks.await


class FirebaseTestResultDao : TestResultDao {
    private val firestore = Firebase.firestore

    override suspend fun saveTestResult(testResult: TestResultDto) {
        firestore.collection("results")
            .document(testResult.testId!!)
            .collection("answers")
            .document(testResult.userId!!)
            .set(testResult)
            .await()
    }

    override suspend fun getTestResult(testId: String, userId: String): TestResultDto? {
        return firestore.collection("results")
            .document(testId)
            .collection("answers")
            .document(userId)
            .get()
            .await()
            .toObject<TestResultDto>()
    }

    override suspend fun getTestResults(
        pageSize: Int,
        testId: String,
        lastDocument: DocumentSnapshot?
    ): FirestorePagedResult<List<TestResultDto>> {
        val result = firestore.collection("results")
            .document(testId)
            .collection("answers")
            .run {
                if (lastDocument != null) {
                    startAfter(lastDocument)
                } else this
            }.limit(pageSize.toLong())
            .get()
            .await()

        val testResults = result.toObjects<TestResultDto>()

        val lastRetrievedDocument = result.documents
            .takeIf {
                it.size == pageSize
            }?.lastOrNull()

        return FirestorePagedResult(
            data = testResults,
            lastDocument = lastRetrievedDocument
        )
    }
}