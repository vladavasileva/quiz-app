/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.vlada.quiz.data.dao.UserAuthDao
import com.vlada.quiz.domain.exception.*
import com.vlada.quiz.domain.model.Credential
import com.vlada.quiz.domain.repository.UserAuthRepository
import kotlinx.coroutines.flow.Flow

class UserAuthRepositoryImpl(
    private val userAuthDao: UserAuthDao,
) : UserAuthRepository {

    private val TAG = UserAuthRepositoryImpl::class.java.simpleName

    override fun observeUserId(): Flow<String?> = userAuthDao.observeUserId()

    override suspend fun getUserId(): String? = userAuthDao.getUserId()

    override suspend fun signUp(credential: Credential) {
        try {
            userAuthDao.signUp(credential.email, credential.password)
        } catch (e: FirebaseNetworkException) {
            throw NetworkException()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw UserAlreadyExistsException()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override suspend fun logIn(credential: Credential) {
        try {
            userAuthDao.logIn(credential.email, credential.password)
        } catch (e: FirebaseNetworkException) {
            throw NetworkException()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw UserDoesNotExistException()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw InvalidCredentialException()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override suspend fun logOut() = userAuthDao.logOut()
}