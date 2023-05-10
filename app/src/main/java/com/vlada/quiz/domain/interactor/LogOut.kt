/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.repository.UserAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogOut(
    private val userAuthRepository: UserAuthRepository
) : Interactor<Unit, Unit>() {
    override suspend fun doWork(params: Unit) = withContext(Dispatchers.IO) {
        userAuthRepository.logOut()
    }
}