/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.util

data class TextFieldState(
    val text: String = "",
    val errorResId: Int? = null
) {
    companion object {
        val Empty = TextFieldState()
    }
}
