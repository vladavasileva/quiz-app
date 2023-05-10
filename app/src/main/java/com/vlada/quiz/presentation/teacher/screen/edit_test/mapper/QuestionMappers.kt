/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test.mapper

import com.vlada.quiz.domain.model.Question
import com.vlada.quiz.presentation.teacher.screen.edit_test.QuestionState
import com.vlada.quiz.presentation.util.TextFieldState

fun QuestionState.toDomainModel() =
    Question(
        id = id,
        imageId = imageId,
        question = question.text,
        answer1 = answer1.text,
        answer2 = answer2.text,
        answer3 = answer3.text,
        answer4 = answer4.text,
        correctAnswer = correctAnswer
    )

fun Question.toState() =
    QuestionState(
        id = id,
        imageId = imageId,
        question = TextFieldState(text = question),
        answer1 = TextFieldState(text = answer1),
        answer2 = TextFieldState(text = answer2),
        answer3 = TextFieldState(text = answer3),
        answer4 = TextFieldState(text = answer4),
        correctAnswer = correctAnswer
    )