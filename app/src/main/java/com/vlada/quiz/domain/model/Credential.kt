/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

data class Credential(
    val email: String = "",
    val password: String = ""
) {
    companion object {
        val Empty = Credential()
    }
}

