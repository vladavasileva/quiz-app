/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.UserRole

@Composable
fun UserRoleSegmentedButtons(
    modifier: Modifier = Modifier,
    selectedRole: UserRole,
    contentColor: Color,
    containerColor: Color,
    onRoleSelected: (UserRole) -> Unit
) {
    SegmentedButtons(
        modifier = modifier,
        options = UserRole.values().toList(),
    ) { userRole ->
        SegmentedButtonsItem(
            label = userRoleToString(userRole),
            isSelected = selectedRole == userRole,
            contentColor = contentColor,
            containerColor = containerColor,
            onOptionSelected = {
                onRoleSelected(userRole)
            }
        )
    }
}

@Composable
private fun userRoleToString(userRole: UserRole): String {
    return when (userRole) {
        UserRole.TEACHER -> stringResource(id = R.string.user_role_teacher)
        UserRole.STUDENT -> stringResource(id = R.string.user_role_student)
    }
}