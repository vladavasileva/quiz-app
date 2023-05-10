/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test

import com.vlada.quiz.presentation.util.UiMessage

data class TeacherEditTestViewState(
    val test: TestState = TestState.Empty,
    val isAddButtonEnabled: Boolean = true,
    val isDeleteButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = TeacherEditTestViewState()
    }
}
