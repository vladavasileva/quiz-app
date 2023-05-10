/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test

import com.vlada.quiz.domain.model.Question
import com.vlada.quiz.presentation.util.UiMessage

data class StudentTestViewState(
    val currentQuestion: Question = Question.Empty,
    val reachedEnd: Boolean = false,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = StudentTestViewState()
    }
}