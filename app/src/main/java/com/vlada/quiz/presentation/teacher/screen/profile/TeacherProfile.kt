/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vlada.quiz.R
import com.vlada.quiz.presentation.compose.AppProgressIndicator
import com.vlada.quiz.presentation.compose.AppSnackbar
import com.vlada.quiz.presentation.compose.ShowSnackbar
import com.vlada.quiz.presentation.compose.TextFieldError
import com.vlada.quiz.presentation.util.popBackStackIfNotLast
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TeacherProfile(
    viewModel: TeacherProfileViewModel = koinViewModel(),
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsState()

    viewState.message?.let { message ->
        ShowSnackbar(
            snackbarHostState = snackbarHostState,
            id = message.id,
            message = stringResource(id = message.resId),
            action = stringResource(id = R.string.hide),
            onAction = {
                snackbarHostState.currentSnackbarData?.dismiss()
            },
            onClose = {
                viewModel.clearMessage(id = message.id)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.teacher_profile_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStackIfNotLast()
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logOut()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = viewState.isSaveButtonVisible,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 250)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 100)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 250)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 200)
                )
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.saveUserDetails()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = viewState.givenName.text,
                        onValueChange = { text ->
                            viewModel.setGivenName(name = text)
                        },
                        supportingText = {
                            TextFieldError(state = viewState.givenName)
                        },
                        isError = viewState.givenName.errorResId != null,
                        label = { Text(stringResource(id = R.string.given_name)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = viewState.familyName.text,
                        onValueChange = { text ->
                            viewModel.setFamilyName(name = text)
                        },
                        supportingText = {
                            TextFieldError(state = viewState.familyName)
                        },
                        isError = viewState.familyName.errorResId != null,
                        label = { Text(stringResource(id = R.string.family_name)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            }
        }
    }

    if (viewState.isLoading) {
        AppProgressIndicator()
    }
}