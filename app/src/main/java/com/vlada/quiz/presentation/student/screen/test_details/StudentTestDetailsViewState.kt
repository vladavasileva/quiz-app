/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test_details

import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.presentation.util.UiMessage

data class StudentTestDetailsViewState(
    val test: Test = Test.Empty,
    val testResult: TestResult = TestResult.Empty,
    val isLoading: Boolean = false,
    val isTestResultLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = StudentTestDetailsViewState()
    }
}