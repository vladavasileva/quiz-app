/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

data class TestResult(
    val testId: String? = null,
    val userDetails: UserDetails = UserDetails.Empty,
    val answers: List<QuestionAnswer> = emptyList()
) {
    companion object {
        val Empty = TestResult()
    }
}