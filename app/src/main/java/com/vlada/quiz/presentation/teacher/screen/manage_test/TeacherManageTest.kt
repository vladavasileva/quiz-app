/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.manage_test

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Plagiarism
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.*
import com.vlada.quiz.presentation.common.navigation.Screen
import com.vlada.quiz.presentation.compose.*
import com.vlada.quiz.presentation.teacher.screen.home.TeacherHomeNavBackTestResult
import com.vlada.quiz.presentation.theme.customGreen
import com.vlada.quiz.presentation.theme.customRed
import com.vlada.quiz.presentation.util.popBackStackIfNotLast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TeacherManageTest(
    viewModel: TeacherManageTestViewModel = koinViewModel(),
    testId: String,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsState()
    val testResults = viewModel.pagedTestResults.collectAsLazyPagingItems()
    val testResultsListState = rememberLazyListState()

    val testsRefreshing by remember {
        derivedStateOf {
            when (testResults.loadState.refresh) {
                is LoadState.Loading -> true
                is LoadState.NotLoading -> false
                else -> false
            }
        }
    }

    val testsAppending by remember {
        derivedStateOf {
            when (testResults.loadState.append) {
                is LoadState.Loading -> true
                is LoadState.NotLoading -> false
                else -> false
            }
        }
    }

    val isListAtTop by remember {
        derivedStateOf {
            testResultsListState.layoutInfo.visibleItemsInfo
                .firstOrNull()?.let { item ->
                    !(item.index == 0 && item.offset == 0)
                } ?: false
        }
    }

    val isScrollUpButtonVisible = testResultsListState.isScrollingUp() && isListAtTop

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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

    when (viewState.dialog) {
        is TeacherManageTestDialog.ConfirmTestDeletion -> {
            ConfirmTestDeletionDialog(
                onConfirm = {
                    viewModel.hideDialog()
                    viewModel.deleteTest {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "navBackTestResult",
                            TeacherHomeNavBackTestResult.DELETED.name
                        )
                        navController.popBackStackIfNotLast()
                    }
                },
                onDismiss = {
                    viewModel.hideDialog()
                }
            )
        }

        else -> {}
    }

    val navigateBack = {
        if (viewState.isTestEdited) {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "navBackTestResult",
                TeacherHomeNavBackTestResult.EDITED.name
            )
        }
        navController.popBackStackIfNotLast()
    }

    BackHandler {
        navigateBack()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.teacher_manage_test_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateBack()
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.setIsTestEdited(isEdited = true)

                            navController.navigate(
                                Screen.TEACHER_EDIT_TEST.route + "?testId=${testId}"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.showDialog(
                                dialog = TeacherManageTestDialog.ConfirmTestDeletion
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollUpButtonVisible,
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
                    onClick = {
                        coroutineScope.launch {
                            testResultsListState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
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
                .padding(top = 10.dp)
        ) {
            Column {
                TopTestBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    imageId = viewState.test.imageId,
                    title = viewState.test.title
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.teacher_manage_test_results),
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(modifier = Modifier.height(1.dp)) {
                    AnimatedVisibility(
                        visible = isListAtTop,
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = 350)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(durationMillis = 350)
                        )
                    ) {
                        Divider()
                    }
                }

                @Suppress("deprecation")
                SwipeRefresh(
                    state = rememberSwipeRefreshState(testsRefreshing),
                    indicator = { state, trigger ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = trigger,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    onRefresh = {
                        testResults.refresh()
                    }
                ) {
                    if (testResults.itemCount == 0 && !testsRefreshing) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(100.dp),
                                imageVector = Icons.Outlined.Plagiarism,
                                contentDescription = ""
                            )

                            Text(text = stringResource(id = R.string.teacher_manage_test_no_one_passed_test))
                        }
                    } else {
                        var expandedItemIndex by remember { mutableStateOf(-1) }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = testResultsListState,
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(testResults.itemCount) { index ->
                                testResults[index]?.let { testResult ->
                                    TestResultItem(
                                        testResult = testResult,
                                        test = viewState.test,
                                        isExpanded = index == expandedItemIndex,
                                        onExpandChanged = { isExpanded ->
                                            expandedItemIndex = if (isExpanded) {
                                                index
                                            } else {
                                                -1
                                            }
                                        }
                                    )
                                }
                            }

                            if (testsAppending) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
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
    test: Test,
    isExpanded: Boolean,
    onExpandChanged: (Boolean) -> Unit
) {
    val questionsNumber = test.questions.size

    val questionAndAnswer by remember(test, testResult) {
        derivedStateOf {
            val testResultAnswers = testResult.answers.associateBy { it.questionId }

            test.questions.mapNotNull { question ->
                testResultAnswers[question.id]?.let { answer ->
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

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column {
            Column(
                modifier = Modifier
                    .clickable {
                        onExpandChanged(!isExpanded)
                    }
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
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
                        text = testResult.userDetails.givenName + " " + testResult.userDetails.familyName,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "$score/$questionsNumber",
                        fontSize = 18.sp
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(150)),
                exit = shrinkVertically(tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
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

@Composable
private fun ConfirmTestDeletionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = stringResource(id = R.string.delete_test_dialog_title))
        },
        text = {
            Text(
                text = stringResource(id = R.string.delete_test_dialog_text),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        dismissButton = {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirm()
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    )
}
