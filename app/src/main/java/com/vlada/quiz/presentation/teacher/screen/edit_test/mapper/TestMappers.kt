/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test.mapper

import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.presentation.teacher.screen.edit_test.TestState
import com.vlada.quiz.presentation.util.TextFieldState

fun TestState.toDomainModel() =
    Test(
        id = id,
        timestamp = timestamp,
        imageId = imageId,
        title = title.text,
        questions = questions.map {
            it.toDomainModel()
        }
    )

fun Test.toState() =
    TestState(
        id = id,
        timestamp = timestamp,
        imageId = imageId,
        title = TextFieldState(text = title),
        questions = questions.map {
            it.toState()
        }
    )