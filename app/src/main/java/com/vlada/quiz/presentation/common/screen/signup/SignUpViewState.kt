/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.signup

import com.vlada.quiz.presentation.util.TextFieldState
import com.vlada.quiz.presentation.util.UiMessage

data class SignUpViewState(
    val email: TextFieldState = TextFieldState.Empty,
    val password: TextFieldState = TextFieldState.Empty,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = SignUpViewState()
    }
}
