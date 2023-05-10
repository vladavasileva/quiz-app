/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor

class ValidatePassword : Interactor<ValidatePassword.Params, Boolean>() {
    override suspend fun doWork(params: Params): Boolean {
        val regex = Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\\d]{8,32}$")
        return regex.matches(params.password)
    }

    data class Params(val password: String)
}