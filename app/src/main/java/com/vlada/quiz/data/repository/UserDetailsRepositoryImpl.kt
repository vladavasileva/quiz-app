/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.vlada.quiz.data.dao.UserDetailsDao
import com.vlada.quiz.data.mapper.toDomainModel
import com.vlada.quiz.data.mapper.toDto
import com.vlada.quiz.domain.exception.NetworkException
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.repository.UserDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


class UserDetailsRepositoryImpl(
    private val remoteUserDetailsDao: UserDetailsDao,
    private val localUserDetailsDao: UserDetailsDao
) : UserDetailsRepository {

    private val TAG = UserDetailsRepositoryImpl::class.java.simpleName

    override fun observeUserDetails(userId: String): Flow<UserDetails?> {
        return localUserDetailsDao.observeUserDetails(userId).map { userDetails ->
            userDetails?.toDomainModel() ?: getUserDetails(userId = userId)
        }.catch { e ->
            Log.e(TAG, e.stackTraceToString())
            emit(null)
        }
    }

    private suspend fun getRemoteUserDetails(userId: String): UserDetails? {
        return try {
            remoteUserDetailsDao.getUserDetails(userId)?.toDomainModel()
        } catch (e: FirebaseNetworkException) {
            throw NetworkException()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    private suspend fun getLocalUserDetails(userId: String): UserDetails? {
        return try {
            localUserDetailsDao.getUserDetails(userId)?.toDomainModel()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    private suspend fun saveRemoteUserDetails(userId: String, userDetails: UserDetails) {
        try {
            remoteUserDetailsDao.saveUserDetails(
                userId = userId,
                userDetails = userDetails.toDto()
            )
        } catch (e: FirebaseNetworkException) {
            throw NetworkException()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    private suspend fun saveLocalUserDetails(userId: String, userDetails: UserDetails) {
        try {
            localUserDetailsDao.saveUserDetails(
                userId = userId,
                userDetails = userDetails.toDto()
            )
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override suspend fun getUserDetails(userId: String): UserDetails? {
        val localUserDetails = try {
            getLocalUserDetails(userId = userId)
        } catch (_: Exception) {
            null
        }
        if(localUserDetails != null) {
            return localUserDetails
        }

        val remoteUserDetails = getRemoteUserDetails(userId = userId)
        if (remoteUserDetails != null) {
            saveLocalUserDetails(userId = userId, userDetails = remoteUserDetails)
        }

        return remoteUserDetails
    }

    override suspend fun saveUserDetails(userId: String, userDetails: UserDetails) {
        saveRemoteUserDetails(userId = userId, userDetails = userDetails)
        saveLocalUserDetails(userId = userId, userDetails = userDetails)
    }
}