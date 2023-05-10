/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.TestsSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortChip(
    modifier: Modifier = Modifier,
    currentTestsSortOption: TestsSortOption,
    onSortSelected: (TestsSortOption) -> Unit,
) {
    Box(modifier) {
        var expanded by remember { mutableStateOf(false) }
        val testsSortOptions = TestsSortOption.values().toList()

        FilterChip(
            selected = true,
            onClick = { expanded = true },
            label = {
                Text(
                    text = sortOptionToString(currentTestsSortOption),
                    modifier = Modifier.animateContentSize(),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SortDropdownMenuContent(
                testsSortOptions = testsSortOptions,
                currentTestsSortOption = currentTestsSortOption,
                onItemClick = {
                    onSortSelected(it)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun SortDropdownMenuContent(
    testsSortOptions: List<TestsSortOption>,
    onItemClick: (TestsSortOption) -> Unit,
    modifier: Modifier = Modifier,
    currentTestsSortOption: TestsSortOption? = null,
) {
    for (sort in testsSortOptions) {
        DropdownMenuItem(
            text = {
                Text(
                    text = sortOptionToString(sort),
                    fontWeight = if (sort == currentTestsSortOption) FontWeight.Bold else null,
                )
            },
            onClick = { onItemClick(sort) },
            modifier = modifier,
        )
    }
}

@Composable
private fun sortOptionToString(testsSortOption: TestsSortOption): String {
    return when (testsSortOption) {
        TestsSortOption.DESCENDING -> stringResource(id = R.string.sort_option_newest)
        TestsSortOption.ASCENDING -> stringResource(id = R.string.sort_option_oldest)
    }
}
