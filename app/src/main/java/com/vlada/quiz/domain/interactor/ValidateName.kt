/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor

class ValidateName : Interactor<ValidateName.Params, Boolean>() {
    override suspend fun doWork(params: Params): Boolean = params.name.isNotEmpty()

    data class Params(val name: String)
}