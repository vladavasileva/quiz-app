/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.repository

import com.vlada.quiz.domain.model.UserDetails
import kotlinx.coroutines.flow.Flow

interface UserDetailsRepository {
    fun observeUserDetails(userId: String): Flow<UserDetails?>

    suspend fun getUserDetails(userId: String): UserDetails?

    suspend fun saveUserDetails(userId: String, userDetails: UserDetails)
}