/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.repository.UserAuthRepository
import com.vlada.quiz.domain.repository.UserDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserDetails(
    private val userAuthRepository: UserAuthRepository,
    private val userDetailsRepository: UserDetailsRepository
) : Interactor<Unit, UserDetails>() {

    override suspend fun doWork(params: Unit): UserDetails = withContext(Dispatchers.IO) {
        val userId = userAuthRepository.getUserId()
            ?: throw UnexpectedException()

        return@withContext userDetailsRepository.getUserDetails(userId = userId)
            ?: throw UnexpectedException()
    }
}