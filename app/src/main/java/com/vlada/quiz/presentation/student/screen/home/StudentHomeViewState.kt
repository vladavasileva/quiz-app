/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.home

import com.vlada.quiz.domain.model.TestsSortOption
import com.vlada.quiz.presentation.util.UiMessage

data class StudentHomeViewState(
    val searchQuery: String = "",
    val testsSortOption: TestsSortOption = TestsSortOption.DESCENDING,
    val isLoading: Boolean = false,
    val message: UiMessage? = null
) {
    companion object {
        val Empty = StudentHomeViewState()
    }
}