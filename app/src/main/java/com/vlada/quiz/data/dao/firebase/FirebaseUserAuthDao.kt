/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vlada.quiz.data.dao.UserAuthDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUserAuthDao : UserAuthDao {
    private val auth = Firebase.auth

    override fun observeUserId(): Flow<String?> = callbackFlow {
        val userIdListener = FirebaseAuth.AuthStateListener { authState ->
            trySend(authState.currentUser?.uid)
        }

        auth.addAuthStateListener(userIdListener)
        awaitClose {
            auth.removeAuthStateListener(userIdListener)
        }
    }

    override suspend fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()
    }

    override suspend fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }

    override fun logOut() {
        auth.signOut()
    }
}