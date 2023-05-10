/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppSnackbar(
    snackbarData: SnackbarData
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = {
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.inversePrimary
                    ),
                    onClick = {
                        snackbarData.performAction()
                    }
                ) {
                    Text(
                        text = actionLabel,
                    )
                }
            }
        }
    ) {
        Text(text = snackbarData.visuals.message)
    }
}