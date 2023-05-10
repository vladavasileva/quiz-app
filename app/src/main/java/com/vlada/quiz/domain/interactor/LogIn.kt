/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.Credential
import com.vlada.quiz.domain.repository.UserAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogIn(
    private val userAuthRepository: UserAuthRepository
) : Interactor<LogIn.Params, Unit>() {

    override suspend fun doWork(params: Params) = withContext(Dispatchers.IO) {
        userAuthRepository.logIn(credential = params.credential)
    }

    data class Params(val credential: Credential)
}