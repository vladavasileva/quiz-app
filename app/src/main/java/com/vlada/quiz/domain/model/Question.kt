/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

data class Question(
    val id: String = "",
    val imageId: String? = null,
    val question: String = "",
    val answer1: String = "",
    val answer2: String = "",
    val answer3: String = "",
    val answer4: String = "",
    val correctAnswer: AnswerOption = AnswerOption.A1
) {
    companion object {
        val Empty = Question()
    }
}
