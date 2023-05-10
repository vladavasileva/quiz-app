/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.test_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.GetTest
import com.vlada.quiz.domain.interactor.GetTestResult
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudentTestDetailsViewModel(
    private val getTest: GetTest,
    private val getTestResult: GetTestResult
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val testResultLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _testResult: MutableStateFlow<TestResult> = MutableStateFlow(TestResult.Empty)

    private val _test: MutableStateFlow<Test> = MutableStateFlow(Test.Empty)

    val state: StateFlow<StudentTestDetailsViewState> = combine(
        _test,
        _testResult,
        loadingState.observable,
        testResultLoadingState.observable,
        uiMessageManager.message
    ) { test, testResult, isLoading, isTestResultLoading, message ->
        StudentTestDetailsViewState(
            test = test,
            testResult = testResult,
            isLoading = isLoading,
            isTestResultLoading = isTestResultLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = StudentTestDetailsViewState.Empty,
    )

    fun loadTest(testId: String) = launchWithLoader(loadingState) {
        getTest(params = GetTest.Params(testId = testId))
            .withErrorMessage(uiMessageManager)
            .onSuccess { test ->
                _test.value = test
            }.collect()
    }

    fun loadTestResult(testId: String) = launchWithLoader(testResultLoadingState) {
        getTestResult(params = GetTestResult.Params(testId = testId))
            .withErrorMessage(uiMessageManager)
            .onSuccess { test ->
                _testResult.value = test
            }.collect()
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id = id)
        }
    }
}