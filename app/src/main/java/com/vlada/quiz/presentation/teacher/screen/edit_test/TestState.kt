/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test

import com.vlada.quiz.presentation.util.TextFieldState

data class TestState(
    val id: String? = null,
    val timestamp: Long = 0L,
    val imageId: String? = null,
    val title: TextFieldState = TextFieldState.Empty,
    val questions: List<QuestionState> = listOf(QuestionState.Empty),
    val currentQuestionNumber: Int = 0
) {
    companion object {
        val Empty = TestState()
    }
}