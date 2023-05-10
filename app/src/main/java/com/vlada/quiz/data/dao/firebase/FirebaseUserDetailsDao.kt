/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vlada.quiz.data.dao.UserDetailsDao
import com.vlada.quiz.data.model.UserDetailsDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseUserDetailsDao : UserDetailsDao {
    private val firestore = Firebase.firestore

    override fun observeUserDetails(userId: String): Flow<UserDetailsDto?> = callbackFlow {
        val userDetailsListener = firestore.collection("users")
            .document(userId).addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject())
            }
        awaitClose { userDetailsListener.remove() }
    }

    override suspend fun getUserDetails(userId: String): UserDetailsDto? {
        return firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject()
    }

    override suspend fun saveUserDetails(userId: String, userDetails: UserDetailsDto) {
        firestore.collection("users")
            .document(userId)
            .set(userDetails)
            .await()
    }
}