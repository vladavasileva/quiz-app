/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.main

data class MainViewState(
    val event: MainEvent? = null
) {
    companion object {
        val Empty = MainViewState()
    }
}
