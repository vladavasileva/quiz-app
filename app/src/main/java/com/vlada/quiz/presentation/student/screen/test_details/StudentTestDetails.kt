/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test_details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.*
import com.vlada.quiz.presentation.common.navigation.Screen
import com.vlada.quiz.presentation.compose.AppProgressIndicator
import com.vlada.quiz.presentation.compose.AppSnackbar
import com.vlada.quiz.presentation.compose.RemoteImage
import com.vlada.quiz.presentation.compose.ShowSnackbar
import com.vlada.quiz.presentation.theme.customGreen
import com.vlada.quiz.presentation.theme.customRed
import com.vlada.quiz.presentation.util.popBackStackIfNotLast
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun StudentTestDetails(
    viewModel: StudentTestDetailsViewModel = koinViewModel(),
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
        viewModel.loadTestResult(testId = testId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.student_test_details_title))
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
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
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
                .padding(vertical = 10.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopTestBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    imageId = viewState.test.imageId,
                    title = viewState.test.title
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        viewState.test.id?.let { testId ->
                            navController.navigate(
                                route = Screen.STUDENT_TEST.route + "/$testId"
                            )
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.student_test_details_start_the_test))
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (viewState.isTestResultLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }
                } else if (viewState.testResult == TestResult.Empty) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(100.dp),
                            imageVector = Icons.Outlined.FactCheck,
                            contentDescription = ""
                        )

                        Text(
                            text = stringResource(
                                id = R.string.student_test_details_not_took_the_test_yet
                            )
                        )
                    }
                } else {
                    TestResultItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        testResult = viewState.testResult,
                        test = viewState.test
                    )
                }
            }
        }
    }

    if (viewState.isLoading) {
        AppProgressIndicator()
    }
}

@Composable
private fun TopTestBar(
    modifier: Modifier = Modifier,
    imageId: String?,
    title: String
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((100f * 2 / 3).dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(3 / 2f),
                contentAlignment = Center
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
                    RemoteImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageId = imageId
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(1f),
                text = title,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun TestResultItem(
    modifier: Modifier = Modifier,
    testResult: TestResult,
    test: Test
) {
    val questionsNumber = test.questions.size

    val questionAndAnswer by remember(test, testResult) {
        derivedStateOf {
            test.questions.mapNotNull { question ->
                val answer = testResult.answers.find { questionAnswer ->
                    question.id == questionAnswer.questionId
                }

                answer?.let {
                    question to answer
                }
            }.toMap()
        }
    }

    val score by remember(questionAndAnswer) {
        derivedStateOf {
            questionAndAnswer.count {
                it.key.correctAnswer == it.value.answer
            }
        }
    }

    Log.e("A", testResult.toString())

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = stringResource(id = R.string.student_test_details_my_result),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "$score/$questionsNumber",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            questionAndAnswer.onEachIndexed { index, (question, answer) ->
                Text(
                    text = "${index + 1}. ${question.question}",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.large)
                            .background(
                                if (question.correctAnswer == answer.answer) {
                                    MaterialTheme.colorScheme.customGreen
                                } else {
                                    MaterialTheme.colorScheme.customRed
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = question.getAnswer(answer.answer),
                        fontSize = 16.sp
                    )
                }

                if (index != questionAndAnswer.size - 1) {
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
    }
}

private fun Question.getAnswer(answerOption: AnswerOption): String {
    return when (answerOption) {
        AnswerOption.A1 -> this.answer1
        AnswerOption.A2 -> this.answer2
        AnswerOption.A3 -> this.answer3
        AnswerOption.A4 -> this.answer4
    }
}