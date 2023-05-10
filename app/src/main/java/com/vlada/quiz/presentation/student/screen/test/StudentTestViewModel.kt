/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.GetTest
import com.vlada.quiz.domain.interactor.SaveTestResult
import com.vlada.quiz.domain.model.*
import com.vlada.quiz.presentation.util.*
import com.vlada.quiz.presentation.util.service.nextElement
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudentTestViewModel(
    private val getTest: GetTest,
    private val saveTestResult: SaveTestResult
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _test: MutableStateFlow<Test> = MutableStateFlow(Test.Empty)
    private val _currentQuestion: MutableStateFlow<Question> = MutableStateFlow(Question.Empty)
    private val _testResult: MutableStateFlow<TestResult> = MutableStateFlow(TestResult.Empty)
    private val _reachedEnd: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: StateFlow<StudentTestViewState> = combine(
        _currentQuestion,
        _reachedEnd,
        loadingState.observable,
        uiMessageManager.message
    ) { currentQuestion, reachedEnd, isLoading, message ->
        StudentTestViewState(
            reachedEnd = reachedEnd,
            currentQuestion = currentQuestion,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = StudentTestViewState.Empty,
    )

    init {
        _test.onEach { test ->
            _test.value = test
            _currentQuestion.value = test.questions.firstOrNull() ?: Question.Empty
            _testResult.update { testResult ->
                testResult.copy(testId = test.id)
            }
        }.launchIn(viewModelScope)
    }

    fun loadTest(testId: String) = launchWithLoader(loadingState) {
        getTest(params = GetTest.Params(testId = testId))
            .withErrorMessage(uiMessageManager)
            .onSuccess { test ->
                _test.value = test.copy(
                    questions = test.questions.shuffled()
                )
            }.collect()
    }

    fun saveTestResult(onSuccess: () -> Unit) {
        saveTestResult(params = SaveTestResult.Params(testResult = _testResult.value))
            .withLoader(loadingState)
            .withErrorMessage(uiMessageManager)
            .onSuccess {
                onSuccess()
            }.launchIn(viewModelScope)
    }

    fun answer(answerOption: AnswerOption) {
        val currentQuestion = _currentQuestion.value

        _testResult.update { testResult ->
            testResult.copy(
                answers = testResult.answers + QuestionAnswer(
                    questionId = currentQuestion.id,
                    answer = answerOption
                )
            )
        }

        val newCurrentQuestion = _test.value.questions.nextElement(currentQuestion)

        if (newCurrentQuestion == null) {
            _reachedEnd.value = true
        } else {
            _currentQuestion.value = newCurrentQuestion
        }
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}