/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test

import com.vlada.quiz.domain.model.AnswerOption
import com.vlada.quiz.presentation.util.TextFieldState
import java.util.*

data class QuestionState(
    val id: String = UUID.randomUUID().toString(),
    val imageId: String? = null,
    val question: TextFieldState = TextFieldState.Empty,
    val answer1: TextFieldState = TextFieldState.Empty,
    val answer2: TextFieldState = TextFieldState.Empty,
    val answer3: TextFieldState = TextFieldState.Empty,
    val answer4: TextFieldState = TextFieldState.Empty,
    val correctAnswer: AnswerOption = AnswerOption.A1
) {
    companion object {
        val Empty = QuestionState()
    }
}