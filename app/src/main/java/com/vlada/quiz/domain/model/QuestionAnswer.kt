/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

data class QuestionAnswer(
    val questionId: String = "",
    val answer: AnswerOption = AnswerOption.A1
) {
    companion object {
        val Empty = QuestionAnswer()
    }
}