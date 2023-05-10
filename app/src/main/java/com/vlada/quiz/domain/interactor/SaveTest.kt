/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.repository.TestRepository
import com.vlada.quiz.domain.repository.UserAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveTest(
    private val testRepository: TestRepository,
    private val userAuthRepository: UserAuthRepository
) : Interactor<SaveTest.Params, Unit>() {

    override suspend fun doWork(params: Params): Unit = withContext(Dispatchers.IO) {
        userAuthRepository.getUserId()?.let { userId ->
            testRepository.saveTest(test = params.test.copy(teacherId = userId))
        }
    }

    data class Params(val test: Test)
}