/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vlada.quiz.presentation.util.TextFieldState

@Composable
fun TextFieldError(state: TextFieldState) {
    state.errorResId?.let { resId ->
        Text(
            text = stringResource(id = resId),
            color = MaterialTheme.colorScheme.error
        )
    }
}