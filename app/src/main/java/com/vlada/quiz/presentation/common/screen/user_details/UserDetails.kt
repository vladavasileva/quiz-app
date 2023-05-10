/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.user_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vlada.quiz.R
import com.vlada.quiz.presentation.compose.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetails(
    viewModel: UserDetailsViewModel = koinViewModel()
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
                            text = stringResource(id = R.string.user_details_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(modifier = Modifier.height(10.dp))

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

                        Spacer(modifier = Modifier.height(10.dp))

                        UserRoleSegmentedButtons(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selectedRole = viewState.userRole,
                            onRoleSelected = { userRole ->
                                viewModel.setUserRole(role = userRole)
                            },
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.saveUserDetails()
                            }
                        ) {
                            Text(stringResource(id = R.string.save))
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
                    Text(text = stringResource(id = R.string.have_another_account))

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                focusManager.clearFocus()
                                viewModel.logOut()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 3.dp),
                            text = stringResource(id = R.string.go_to_log_in_screen),
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