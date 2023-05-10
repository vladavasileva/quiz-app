/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vlada.quiz.data.dao.UserDetailsDao
import com.vlada.quiz.data.mapper.toDomainModel
import com.vlada.quiz.data.mapper.toDto
import com.vlada.quiz.data.model.UserDetailsDto
import com.vlada.quiz.domain.model.UserDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

val Context.userDetailsDataStore by preferencesDataStore(name = "UserDetails")

class LocalUserDetailsDao(private val context: Context) : UserDetailsDao {

    override fun observeUserDetails(userId: String): Flow<UserDetailsDto?> {
        val userDetailsKey = stringPreferencesKey(userId)
        return context.userDetailsDataStore.data.map { preferences ->
            preferences[userDetailsKey]?.let { userDetailsJson ->
                try {
                    Json.decodeFromString(UserDetails.serializer(), userDetailsJson).toDto()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun getUserDetails(userId: String): UserDetailsDto? {
        val userDetailsKey = stringPreferencesKey(userId)
        return context.userDetailsDataStore.data.firstOrNull()?.get(userDetailsKey)
            ?.let { userDetailsJson ->
                Json.decodeFromString(UserDetails.serializer(), userDetailsJson).toDto()
            }
    }

    override suspend fun saveUserDetails(userId: String, userDetails: UserDetailsDto) {
        val userDetailsKey = stringPreferencesKey(userId)
        context.userDetailsDataStore.edit { preferences ->
            preferences.clear()
            val userDetailsJson =
                Json.encodeToString(UserDetails.serializer(), userDetails.toDomainModel())
            preferences[userDetailsKey] = userDetailsJson
        }
    }
}