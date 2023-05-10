/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun ViewModel.launchWithLoader(
    loadingCounter: ObservableLoadingCounter,
    block: suspend () -> Unit
) =
    viewModelScope.launch {
        loadingCounter.addLoader()
        try {
            block()
        } finally {
            loadingCounter.removeLoader()
        }
    }