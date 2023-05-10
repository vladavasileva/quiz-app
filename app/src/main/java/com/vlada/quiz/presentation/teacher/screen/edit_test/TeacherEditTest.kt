/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.vlada.quiz.R
import com.vlada.quiz.domain.model.AnswerOption
import com.vlada.quiz.presentation.compose.*
import com.vlada.quiz.presentation.teacher.screen.home.TeacherHomeNavBackTestResult
import com.vlada.quiz.presentation.util.TextFieldState
import com.vlada.quiz.presentation.util.popBackStackIfNotLast
import com.vlada.quiz.presentation.util.service.ImageService
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TeacherEditTest(
    viewModel: TeacherEditTestViewModel = koinViewModel(),
    testId: String? = null,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsState()

    val imageService: ImageService = get()

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

    val pickTestImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = imageService.uriToBitmap(context = context, uri = uri)
                val resizedBitmap = imageService.resizeImage(
                    bitmap = bitmap,
                    maxWidth = 500,
                    maxHeight = 500
                )
                val imageBytes = imageService.bitmapToByteArray(bitmap = resizedBitmap)

                viewModel.addTestImage(image = imageBytes)
            } catch (e: Exception) {
                viewModel.showMessage(resId = R.string.error_failed_to_load_the_image)
            }
        }
    }

    val pickQuestionImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = imageService.uriToBitmap(context = context, uri = uri)
                val resizedBitmap = imageService.resizeImage(
                    bitmap = bitmap,
                    maxWidth = 1000,
                    maxHeight = 1000
                )
                val imageBytes = imageService.bitmapToByteArray(bitmap = resizedBitmap)

                viewModel.addQuestionImage(image = imageBytes)
            } catch (e: Exception) {
                viewModel.showMessage(resId = R.string.error_failed_to_load_the_image)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (!result) {
            viewModel.showMessage(resId = R.string.error_storage_permission_declined)
        }
    }

    LaunchedEffect(testId) {
        testId?.let { id ->
            viewModel.loadTest(testId = id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.edit_test_title))
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
                            viewModel.saveTest {
                                val isCreatingNewTest = viewState.test.id == null

                                if (isCreatingNewTest) {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "navBackTestResult",
                                        TeacherHomeNavBackTestResult.CREATED.name
                                    )
                                }

                                navController.popBackStackIfNotLast()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopTestBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    imageId = viewState.test.imageId,
                    title = viewState.test.title,
                    onImageClicked = {
                        if (isStoragePermissionGranted(context = context)) {
                            pickTestImageLauncher.launch("image/*")
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    onTextChanged = { text ->
                        viewModel.setTestTitle(text = text)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            viewModel.deleteQuestion()
                        },
                        enabled = viewState.isDeleteButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.addQuestion()
                        },
                        enabled = viewState.isAddButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }

                var cardSwitchDirection by remember(viewState.test.questions.size) {
                    mutableStateOf(-1)
                }

                AnimatedContent(
                    modifier = Modifier
                        .weight(1f),
                    targetState = viewState.test.currentQuestionNumber,
                    transitionSpec = {
                        ContentTransform(
                            targetContentEnter = slideInHorizontally { height ->
                                cardSwitchDirection * -height
                            } + fadeIn(),
                            initialContentExit = slideOutHorizontally { height ->
                                cardSwitchDirection * height
                            } + fadeOut()
                        )
                    }
                ) { currentQuestionNumber ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        viewState.test.questions.getOrNull(currentQuestionNumber)
                            ?.let { currentQuestion ->
                                QuestionCard(
                                    state = currentQuestion,
                                    onImageClicked = {
                                        if (isStoragePermissionGranted(context = context)) {
                                            pickQuestionImageLauncher.launch("image/*")
                                        } else {
                                            permissionLauncher.launch(
                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                            )
                                        }
                                    },
                                    onStateChanged = { question ->
                                        viewModel.setQuestion(question = question)
                                    }
                                )
                            }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            cardSwitchDirection = 1
                            viewModel.moveLeft()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(
                            id = R.string.edit_test_of,
                            viewState.test.currentQuestionNumber + 1,
                            viewState.test.questions.size
                        )
                    )

                    IconButton(
                        onClick = {
                            cardSwitchDirection = -1
                            viewModel.moveRight()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopTestBar(
    modifier: Modifier = Modifier,
    imageId: String?,
    title: TextFieldState,
    onImageClicked: () -> Unit,
    onTextChanged: (String) -> Unit
) {
    val localDensity = LocalDensity.current

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var rowHeight by remember { mutableStateOf(0.dp) }

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(rowHeight)
                    .clickable {
                        onImageClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageId == null) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.7f),
                        imageVector = Icons.Default.AddPhotoAlternate,
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

            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 8.dp)
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        rowHeight = with(localDensity) {
                            coordinates.parentCoordinates?.size?.height?.toDp() ?: 0.dp
                        }
                    },
                value = title.text,
                onValueChange = { text ->
                    onTextChanged(text)
                },
                supportingText = title.errorResId?.let {
                    {
                        TextFieldError(state = title)
                    }
                },
                isError = title.errorResId != null,
                label = { Text(stringResource(id = R.string.teacher_edit_test_test_title)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionCard(
    modifier: Modifier = Modifier,
    state: QuestionState,
    onImageClicked: () -> Unit,
    onStateChanged: (QuestionState) -> Unit
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(3 / 2f)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        onImageClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageId == null) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.7f),
                        imageVector = Icons.Default.AddPhotoAlternate,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                } else {
                    RemoteImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageId = state.imageId
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = state.question.text,
                onValueChange = { text ->
                    onStateChanged(
                        state.copy(
                            question = state.question.copy(
                                text = text,
                                errorResId = null
                            )
                        )
                    )
                },
                supportingText = {
                    TextFieldError(state = state.question)
                },
                isError = state.question.errorResId != null,
                label = { Text(stringResource(id = R.string.question)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            AnswerBox(
                answer = state.answer1,
                answerNumber = AnswerOption.A1,
                pickedAnswer = state.correctAnswer,
                onAnswerPicked = {
                    onStateChanged(state.copy(correctAnswer = AnswerOption.A1))
                },
                onAnswerChanged = { text ->
                    onStateChanged(
                        state.copy(
                            answer1 = state.answer1.copy(
                                text = text,
                                errorResId = null
                            )
                        )
                    )
                }
            )

            AnswerBox(
                answer = state.answer2,
                answerNumber = AnswerOption.A2,
                pickedAnswer = state.correctAnswer,
                onAnswerPicked = {
                    onStateChanged(state.copy(correctAnswer = AnswerOption.A2))
                },
                onAnswerChanged = { text ->
                    onStateChanged(
                        state.copy(
                            answer2 = state.answer2.copy(
                                text = text,
                                errorResId = null
                            )
                        )
                    )
                }
            )

            AnswerBox(
                answer = state.answer3,
                answerNumber = AnswerOption.A3,
                pickedAnswer = state.correctAnswer,
                onAnswerPicked = {
                    onStateChanged(state.copy(correctAnswer = AnswerOption.A3))
                },
                onAnswerChanged = { text ->
                    onStateChanged(
                        state.copy(
                            answer3 = state.answer3.copy(
                                text = text,
                                errorResId = null
                            )
                        )
                    )
                }
            )

            AnswerBox(
                answer = state.answer4,
                answerNumber = AnswerOption.A4,
                pickedAnswer = state.correctAnswer,
                onAnswerPicked = {
                    onStateChanged(state.copy(correctAnswer = AnswerOption.A4))
                },
                onAnswerChanged = { text ->
                    onStateChanged(
                        state.copy(
                            answer4 = state.answer4.copy(
                                text = text,
                                errorResId = null
                            )
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerBox(
    answer: TextFieldState,
    answerNumber: AnswerOption,
    pickedAnswer: AnswerOption,
    onAnswerPicked: () -> Unit,
    onAnswerChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RadioButton(
            selected = pickedAnswer == answerNumber,
            onClick = {
                onAnswerPicked()
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = answer.text,
            onValueChange = { text ->
                onAnswerChanged(text)
            },
            supportingText = {
                TextFieldError(state = answer)
            },
            isError = answer.errorResId != null,
            label = { Text(stringResource(id = R.string.answer)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )
    }
}

private fun isStoragePermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        true
    } else {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}