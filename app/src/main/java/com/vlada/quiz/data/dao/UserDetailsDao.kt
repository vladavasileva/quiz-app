/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao

import com.vlada.quiz.data.model.UserDetailsDto
import kotlinx.coroutines.flow.Flow

interface UserDetailsDao {
    fun observeUserDetails(userId: String): Flow<UserDetailsDto?>

    suspend fun getUserDetails(userId: String): UserDetailsDto?

    suspend fun saveUserDetails(userId: String, userDetails: UserDetailsDto)
}