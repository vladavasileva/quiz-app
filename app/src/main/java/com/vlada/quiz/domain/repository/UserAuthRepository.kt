/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.repository

import com.vlada.quiz.domain.model.Credential
import kotlinx.coroutines.flow.Flow

interface UserAuthRepository {
    fun observeUserId(): Flow<String?>

    suspend fun getUserId(): String?

    suspend fun signUp(credential: Credential)

    suspend fun logIn(credential: Credential)

    suspend fun logOut()
}