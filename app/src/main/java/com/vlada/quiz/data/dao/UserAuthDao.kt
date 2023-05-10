/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao

import kotlinx.coroutines.flow.Flow

interface UserAuthDao {
    fun observeUserId(): Flow<String?>

    suspend fun getUserId(): String?

    suspend fun signUp(email: String, password: String)

    suspend fun logIn(email: String, password: String)

    fun logOut()
}