/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.profile

import com.vlada.quiz.presentation.util.TextFieldState
import com.vlada.quiz.presentation.util.UiMessage

data class TeacherProfileViewState(
    val givenName: TextFieldState = TextFieldState.Empty,
    val familyName: TextFieldState = TextFieldState.Empty,
    val isSaveButtonVisible: Boolean = false,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = TeacherProfileViewState()
    }
}
