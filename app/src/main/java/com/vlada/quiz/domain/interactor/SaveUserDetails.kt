/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.repository.UserAuthRepository
import com.vlada.quiz.domain.repository.UserDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveUserDetails(
    private val userAuthRepository: UserAuthRepository,
    private val userDetailsRepository: UserDetailsRepository
) : Interactor<SaveUserDetails.Params, Unit>() {

    override suspend fun doWork(params: Params) {
        withContext(Dispatchers.IO) {
            userAuthRepository.getUserId()?.let { userId ->
                userDetailsRepository.saveUserDetails(
                    userId = userId,
                    userDetails = params.userDetails.copy(userId = userId)
                )
            }
        }
    }

    data class Params(val userDetails: UserDetails)
}