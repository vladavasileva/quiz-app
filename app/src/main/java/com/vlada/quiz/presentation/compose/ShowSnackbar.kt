/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackbar(
    snackbarHostState: SnackbarHostState,
    id: Long,
    message: String,
    action: String? = null,
    onAction: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    LaunchedEffect(id) {
        val snackbarResult = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = action,
            duration = SnackbarDuration.Short
        )

        when (snackbarResult) {
            SnackbarResult.ActionPerformed -> {
                onAction()
            }

            SnackbarResult.Dismissed -> {
                onDismiss()
            }
        }

        onClose()
    }
}