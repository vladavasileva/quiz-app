/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.model

import com.vlada.quiz.domain.model.AnswerOption

data class TestResultDto(
    val testId: String? = null,
    val userId: String? = null,
    val answers: Map<String, AnswerOption> = emptyMap()
)