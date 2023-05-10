/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vlada.quiz.R
import com.vlada.quiz.presentation.common.navigation.Screen
import com.vlada.quiz.presentation.compose.AppProgressIndicator
import com.vlada.quiz.presentation.compose.AppSnackbar
import com.vlada.quiz.presentation.compose.ShowSnackbar
import com.vlada.quiz.presentation.compose.TextFieldError
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Login(
    viewModel: LoginViewModel = koinViewModel(),
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .wrapContentSize(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 2.dp,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(id = R.string.log_in_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = viewState.email.text,
                            onValueChange = { text ->
                                viewModel.setEmail(email = text)
                            },
                            supportingText = {
                                TextFieldError(state = viewState.email)
                            },
                            isError = viewState.email.errorResId != null,
                            label = { Text(stringResource(id = R.string.email)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        var isPasswordVisible by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = viewState.password.text,
                            onValueChange = { text ->
                                viewModel.setPassword(password = text)
                            },
                            supportingText = {
                                TextFieldError(state = viewState.password)
                            },
                            isError = viewState.password.errorResId != null,
                            label = { Text(stringResource(id = R.string.password)) },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.logIn()
                                }
                            ),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        isPasswordVisible = !isPasswordVisible
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = if (isPasswordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = ""
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.logIn()
                            }
                        ) {
                            Text(stringResource(id = R.string.log_in))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    mainAxisSpacing = 2.dp,
                    mainAxisAlignment = FlowMainAxisAlignment.Center,
                    crossAxisAlignment = FlowCrossAxisAlignment.Center
                ) {
                    Text(text = stringResource(id = R.string.do_not_have_an_account))

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                focusManager.clearFocus()
                                navController.navigate(Screen.SIGN_UP.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 3.dp),
                            text = stringResource(id = R.string.go_to_sign_up_screen),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (viewState.isLoading) {
        AppProgressIndicator()
    }
}