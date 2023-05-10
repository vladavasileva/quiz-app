/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun <T> SegmentedButtons(
    modifier: Modifier = Modifier,
    options: List<T>,
    content: @Composable RowScope.(T) -> Unit
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
    ) {
        options.forEachIndexed { index, option ->
            content(option)

            if (options.lastIndex != index) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun RowScope.SegmentedButtonsItem(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    contentColor: Color,
    containerColor: Color,
    onOptionSelected: () -> Unit
) {
    val (optionColor, optionContentColor) = if (isSelected) {
        Pair(containerColor, contentColor)
    } else {
        Pair(Color.Transparent, containerColor)
    }

    OutlinedButton(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = optionColor
        ),
        shape = RectangleShape,
        border = BorderStroke(width = 0.dp, color = Color.Transparent),
        onClick = {
            onOptionSelected()
        }
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = optionContentColor
            )

            Spacer(modifier = Modifier.width(5.dp))
        }

        Text(
            text = label,
            color = optionContentColor,
            overflow = TextOverflow.Ellipsis
        )
    }
}