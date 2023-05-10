/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.user_details

import com.vlada.quiz.domain.model.UserRole
import com.vlada.quiz.presentation.util.TextFieldState
import com.vlada.quiz.presentation.util.UiMessage

data class UserDetailsViewState(
    val givenName: TextFieldState = TextFieldState.Empty,
    val familyName: TextFieldState = TextFieldState.Empty,
    val userRole: UserRole = UserRole.STUDENT,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = UserDetailsViewState()
    }
}
