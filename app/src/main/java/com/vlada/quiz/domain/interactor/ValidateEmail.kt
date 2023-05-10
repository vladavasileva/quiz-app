/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor

class ValidateEmail : Interactor<ValidateEmail.Params, Boolean>() {
    override suspend fun doWork(params: Params): Boolean {
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(params.email)
    }

    data class Params(val email: String)
}