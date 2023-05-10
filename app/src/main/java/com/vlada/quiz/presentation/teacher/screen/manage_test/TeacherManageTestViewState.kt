/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.manage_test

import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.presentation.util.UiMessage

data class TeacherManageTestViewState(
    val test: Test = Test.Empty,
    val dialog: TeacherManageTestDialog? = null,
    val isTestEdited: Boolean = false,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = TeacherManageTestViewState()
    }
}