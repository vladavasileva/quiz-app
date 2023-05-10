/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Plagiarism
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.vlada.quiz.presentation.common.navigation.Screen
import com.vlada.quiz.presentation.compose.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val REFRESH_TESTS_ACTION_TAG = "REFRESH_TESTS"

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TeacherHome(
    viewModel: TeacherHomeViewModel = koinViewModel(),
    navBackTestResult: TeacherHomeNavBackTestResult? = null,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsState()
    val tests = viewModel.pagedTests.collectAsLazyPagingItems()
    val testsGridState = rememberLazyStaggeredGridState()

    val testsRefreshing by remember {
        derivedStateOf {
            when (tests.loadState.refresh) {
                is LoadState.Loading -> true
                is LoadState.NotLoading -> false
                else -> false
            }
        }
    }

    val testsAppending by remember {
        derivedStateOf {
            when (tests.loadState.append) {
                is LoadState.Loading -> true
                is LoadState.NotLoading -> false
                else -> false
            }
        }
    }

    val isGridAtTop by remember {
        derivedStateOf {
            testsGridState.layoutInfo.visibleItemsInfo
                .firstOrNull()?.let { item ->
                    !(item.index == 0 && item.offset.y == 0)
                } ?: false
        }
    }

    val isScrollUpButtonVisible = testsGridState.isScrollingUp() && isGridAtTop

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    viewState.message?.let { message ->
        when (message.actionTag) {
            REFRESH_TESTS_ACTION_TAG -> {
                ShowSnackbar(
                    snackbarHostState = snackbarHostState,
                    id = message.id,
                    message = stringResource(id = message.resId),
                    action = stringResource(id = R.string.refresh),
                    onAction = {
                        tests.refresh()
                    },
                    onClose = {
                        viewModel.clearMessage(id = message.id)
                    }
                )
            }

            else -> {
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
        }
    }

    LaunchedEffect(navBackTestResult) {
        when (navBackTestResult) {
            TeacherHomeNavBackTestResult.CREATED -> {
                viewModel.showMessage(
                    resId = R.string.teacher_home_created_new_test,
                    actionTag = REFRESH_TESTS_ACTION_TAG
                )
            }

            TeacherHomeNavBackTestResult.EDITED -> {
                viewModel.showMessage(
                    resId = R.string.teacher_home_edited_test,
                    actionTag = REFRESH_TESTS_ACTION_TAG
                )
            }

            TeacherHomeNavBackTestResult.DELETED -> {
                viewModel.showMessage(
                    resId = R.string.teacher_home_deleted_test,
                    actionTag = REFRESH_TESTS_ACTION_TAG
                )
            }

            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.TEACHER_PROFILE.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                testsGridState.animateScrollToItem(0)
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

                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.TEACHER_EDIT_TEST.route)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
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
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = viewState.searchQuery,
                    singleLine = true,
                    onValueChange = { text ->
                        viewModel.setSearchQuery(query = text)
                    },
                    label = { Text(stringResource(id = R.string.teacher_home_search_tests)) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.setSearchQuery(query = "")
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Default.Clear,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.teacher_home_my_tests),
                        fontSize = 20.sp
                    )

                    SortChip(
                        currentTestsSortOption = viewState.testsSortOption,
                        onSortSelected = { sortOption ->
                            viewModel.setSortOption(testsSortOption = sortOption)
                            coroutineScope.launch {
                                testsGridState.scrollToItem(0)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(modifier = Modifier.height(1.dp)) {
                    AnimatedVisibility(
                        visible = isGridAtTop,
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
                        tests.refresh()
                    }
                ) {
                    if (tests.itemCount == 0 && !testsRefreshing) {
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

                            val message = if(viewState.searchQuery.isBlank()) {
                                stringResource(id = R.string.teacher_home_not_created_tests)
                            } else {
                                stringResource(id = R.string.teacher_home_no_tests_found)
                            }

                            Text(text = message)
                        }
                    } else {
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = testsGridState,
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tests.itemCount) { index ->
                                tests[index]?.let { test ->
                                    TestItem(
                                        imageId = test.imageId,
                                        title = test.title,
                                        onClick = {
                                            navController.navigate(
                                                Screen.TEACHER_MANAGE_TEST.route + "/${test.id}"
                                            )
                                        }
                                    )
                                }
                            }

                            if (testsAppending) {
                                item {
                                    Box(
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
private fun TestItem(
    modifier: Modifier = Modifier,
    imageId: String?,
    title: String,
    onClick: () -> Unit
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
                .fillMaxWidth(0.5f)
                .clickable {
                    onClick()
                },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3 / 2f),
                contentAlignment = Alignment.Center
            ) {
                if (imageId == null) {
                    Icon(
                        modifier = Modifier
                            .size(50.dp),
                        imageVector = Icons.Default.HideImage,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                } else {
                    RemoteImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        imageId = imageId
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                modifier = Modifier
                    .padding(all = 10.dp),
                text = title,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}