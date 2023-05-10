/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.edit_test

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.R
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.GetTest
import com.vlada.quiz.domain.interactor.SaveTest
import com.vlada.quiz.domain.interactor.UploadImage
import com.vlada.quiz.domain.interactor.ValidateTest
import com.vlada.quiz.domain.model.AnswerOption
import com.vlada.quiz.presentation.teacher.screen.edit_test.mapper.toDomainModel
import com.vlada.quiz.presentation.teacher.screen.edit_test.mapper.toState
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TeacherEditTestViewModel(
    private val getTest: GetTest,
    private val uploadImage: UploadImage,
    private val validateTest: ValidateTest,
    private val saveTest: SaveTest
) : ViewModel() {

    companion object {
        const val MAX_QUESTIONS = 50
    }

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val uiTest: MutableStateFlow<TestState> = MutableStateFlow(TestState.Empty)

    val state: StateFlow<TeacherEditTestViewState> = combine(
        uiTest,
        loadingState.observable,
        uiMessageManager.message
    ) { uiTest, isLoading, message ->
        TeacherEditTestViewState(
            test = uiTest,
            isAddButtonEnabled = uiTest.questions.size < MAX_QUESTIONS,
            isDeleteButtonEnabled = uiTest.questions.size > 1,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TeacherEditTestViewState.Empty,
    )

    fun loadTest(testId: String) = launchWithLoader(loadingState) {
        getTest(params = GetTest.Params(testId = testId))
            .withErrorMessage(uiMessageManager)
            .onSuccess { test ->
                uiTest.value = test.toState()
            }.collect()
    }

    fun setTestTitle(text: String) {
        uiTest.update { test ->
            test.copy(
                title = test.title.copy(text = text, errorResId = null)
            )
        }
    }

    fun moveLeft() {
        uiTest.update { test ->
            if (test.currentQuestionNumber - 1 < 0) {
                test.copy(currentQuestionNumber = test.questions.lastIndex)
            } else {
                test.copy(currentQuestionNumber = test.currentQuestionNumber - 1)
            }
        }
    }

    fun moveRight() {
        uiTest.update { test ->
            if (test.currentQuestionNumber + 1 > test.questions.lastIndex) {
                test.copy(currentQuestionNumber = 0)
            } else {
                test.copy(currentQuestionNumber = test.currentQuestionNumber + 1)
            }
        }
    }

    fun setQuestion(question: QuestionState) {
        uiTest.update { test ->
            val newQuestions = test.questions.toMutableList()
            newQuestions[test.currentQuestionNumber] = question
            test.copy(questions = newQuestions)
        }
    }

    fun addQuestion() {
        uiTest.update { test ->
            if (test.questions.size < MAX_QUESTIONS) {
                val newQuestions = test.questions.toMutableList()
                newQuestions.add(test.currentQuestionNumber + 1, QuestionState.Empty)
                test.copy(
                    questions = newQuestions,
                    currentQuestionNumber = test.currentQuestionNumber + 1
                )
            } else {
                test
            }
        }
    }

    fun deleteQuestion() {
        uiTest.update { test ->
            if (test.questions.size > 1) {
                val newQuestions = test.questions.toMutableList()
                newQuestions.removeAt(test.currentQuestionNumber)
                test.copy(
                    questions = newQuestions,
                    currentQuestionNumber = if (test.currentQuestionNumber != 0) {
                        test.currentQuestionNumber - 1
                    } else {
                        0
                    }
                )
            } else {
                test
            }
        }
    }

    fun addTestImage(image: ByteArray) = launchWithLoader(loadingState) {
        val imageId = UUID.randomUUID().toString()

        uploadImage(
            params = UploadImage.Params(
                image = image,
                imageId = imageId
            )
        ).withErrorMessage(uiMessageManager)
            .onSuccess {
                uiTest.update { test ->
                    test.copy(imageId = imageId)
                }
            }.collect()
    }

    fun addQuestionImage(image: ByteArray) = launchWithLoader(loadingState) {
        val imageId = UUID.randomUUID().toString()
        val currentQuestionNumber = uiTest.value.currentQuestionNumber

        uploadImage(
            params = UploadImage.Params(
                image = image,
                imageId = imageId
            )
        ).withErrorMessage(uiMessageManager)
            .onSuccess {
                uiTest.update { t ->
                    val newQuestions = t.questions.toMutableList()
                    newQuestions[currentQuestionNumber] =
                        newQuestions[currentQuestionNumber].copy(imageId = imageId)
                    t.copy(questions = newQuestions)
                }
            }.collect()
    }

    fun saveTest(onSuccess: () -> Unit) = launchWithLoader(loadingState) {
        val uiTest = uiTest.value
        val test = uiTest.toDomainModel()

        when (
            val status = validateTest.executeSync(params = ValidateTest.Params(test = test))
        ) {
            is ValidateTest.Result.Valid -> {}

            is ValidateTest.Result.Error -> {
                var newTitle = uiTest.title
                val newQuestions = uiTest.questions.toMutableList()
                var newCurrentQuestionNumber = uiTest.currentQuestionNumber

                status.errors.forEach { error ->
                    when (error) {
                        ValidateTest.ValidationError.EmptyTitle -> {
                            newTitle = TextFieldState(
                                text = test.title,
                                errorResId = R.string.error_empty_test_title
                            )
                        }

                        is ValidateTest.ValidationError.EmptyQuestion -> {
                            if (test.questions.size < error.questionIndex) return@forEach

                            newQuestions[error.questionIndex] =
                                newQuestions[error.questionIndex].let { question ->
                                    question.copy(
                                        question = question.question.copy(
                                            errorResId = R.string.error_empty_test_question
                                        )
                                    )
                                }
                        }

                        is ValidateTest.ValidationError.EmptyAnswer -> {
                            if (test.questions.size < error.questionIndex) return@forEach

                            newQuestions[error.questionIndex] =
                                newQuestions[error.questionIndex].let { question ->
                                    when (error.answer) {
                                        AnswerOption.A1 -> question.copy(
                                            answer1 = question.answer1.copy(
                                                errorResId = R.string.error_empty_test_answer
                                            )
                                        )
                                        AnswerOption.A2 -> question.copy(
                                            answer2 = question.answer2.copy(
                                                errorResId = R.string.error_empty_test_answer
                                            )
                                        )
                                        AnswerOption.A3 -> question.copy(
                                            answer3 = question.answer3.copy(
                                                errorResId = R.string.error_empty_test_answer
                                            )
                                        )
                                        AnswerOption.A4 -> question.copy(
                                            answer4 = question.answer4.copy(
                                                errorResId = R.string.error_empty_test_answer
                                            )
                                        )
                                    }
                                }
                        }
                    }
                }

                status.errors.firstOrNull { it !is ValidateTest.ValidationError.EmptyTitle }
                    ?.let { error ->
                        newCurrentQuestionNumber = when (error) {
                            is ValidateTest.ValidationError.EmptyAnswer -> {
                                error.questionIndex
                            }

                            is ValidateTest.ValidationError.EmptyQuestion -> {
                                error.questionIndex
                            }

                            else -> {
                                newCurrentQuestionNumber
                            }
                        }
                    }

                this@TeacherEditTestViewModel.uiTest.update {
                    it.copy(
                        title = newTitle,
                        questions = newQuestions,
                        currentQuestionNumber = newCurrentQuestionNumber
                    )
                }
                return@launchWithLoader
            }
        }

        saveTest(params = SaveTest.Params(test = test))
            .withErrorMessage(uiMessageManager)
            .onSuccess {
                onSuccess()
            }.collect()
    }

    fun showMessage(@StringRes resId: Int) {
        viewModelScope.launch {
            uiMessageManager.emitMessage(UiMessage(resId = resId))
        }
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id = id)
        }
    }
}