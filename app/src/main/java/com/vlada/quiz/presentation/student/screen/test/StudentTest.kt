/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material3.*
import androidx.compose.material3.ScaffoldDefaults.contentWindowInsets
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.AnswerOption
import com.vlada.quiz.domain.model.Question
import com.vlada.quiz.presentation.compose.*
import com.vlada.quiz.presentation.util.popBackStackIfNotLast
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun StudentTest(
    viewModel: StudentTestViewModel = koinViewModel(),
    testId: String,
    navController: NavController
) {
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

    LaunchedEffect(testId) {
        viewModel.loadTest(testId = testId)
    }

    var isInputBlocked by remember { mutableStateOf(false) }

    LaunchedEffect(viewState.currentQuestion) {
        isInputBlocked = true
        delay(500L)
        isInputBlocked = false
    }

    LaunchedEffect(viewState.reachedEnd) {
        if (viewState.reachedEnd) {
            viewModel.saveTestResult {
                navController.popBackStackIfNotLast()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.student_test_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStackIfNotLast()
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
            }
        },
        contentWindowInsets = contentWindowInsets
    ) { contentPadding ->
        val currentQuestion = viewState.currentQuestion
        if (currentQuestion != Question.Empty) {
            AnimatedContent(
                targetState = currentQuestion
            ) { question ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(contentPadding)
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuestionBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        imageId = question.imageId,
                        question = question.question
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            AnswerBox(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                text = question.answer1
                            ) {
                                viewModel.answer(answerOption = AnswerOption.A1)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            AnswerBox(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                text = question.answer2
                            ) {
                                viewModel.answer(answerOption = AnswerOption.A2)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            AnswerBox(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                text = question.answer3
                            ) {
                                viewModel.answer(answerOption = AnswerOption.A3)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            AnswerBox(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                text = question.answer4
                            ) {
                                viewModel.answer(answerOption = AnswerOption.A4)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isInputBlocked) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {}
    }

    if (viewState.isLoading) {
        AppProgressIndicator()
    }
}

@Composable
private fun QuestionBox(
    modifier: Modifier = Modifier,
    imageId: String?,
    question: String
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var isFullTestImageVisible by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                if (imageId == null) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.7f),
                        imageVector = Icons.Default.HideImage,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                } else {
                    ZoomableBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                isFullTestImageVisible = !isFullTestImageVisible
                            },
                        contentAlignment = Alignment.Center,
                        key = isFullTestImageVisible
                    ) {
                        RemoteImage(
                            modifier = Modifier
                                .run {
                                    if (isFullTestImageVisible) {
                                        graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offsetX,
                                            translationY = offsetY
                                        )
                                    } else {
                                        fillMaxSize()
                                    }
                                },
                            imageId = imageId
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                AutoResizeText(
                    text = question,
                    fontSizeRange = FontSizeRange(
                        min = 8.sp,
                        max = 22.sp,
                    ),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AnswerBox(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            AutoResizeText(
                text = text,
                fontSizeRange = FontSizeRange(
                    min = 8.sp,
                    max = 22.sp,
                ),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}