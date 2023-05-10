/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.manage_test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.DeleteTest
import com.vlada.quiz.domain.interactor.GetTest
import com.vlada.quiz.domain.interactor.SaveTestResult
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.observer.ObservePagedTestResults
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TeacherManageTestViewModel(
    private val getTest: GetTest,
    private val deleteTest: DeleteTest,
    observePagedTestResults: ObservePagedTestResults
) : ViewModel() {

    companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20
        )
    }

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val pagedTestResults: Flow<PagingData<TestResult>> =
        observePagedTestResults.flow.cachedIn(viewModelScope)

    private val _isTestEdited: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _test: MutableStateFlow<Test> = MutableStateFlow(Test.Empty)

    private val _dialog: MutableStateFlow<TeacherManageTestDialog?> = MutableStateFlow(null)

    val state: StateFlow<TeacherManageTestViewState> = combine(
        _test,
        _dialog,
        _isTestEdited,
        loadingState.observable,
        uiMessageManager.message
    ) { test, dialog, isTestEdited, isLoading, message ->
        TeacherManageTestViewState(
            test = test,
            dialog = dialog,
            isTestEdited = isTestEdited,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TeacherManageTestViewState.Empty,
    )

    init {
        _test.onEach { test ->
            if (test.id == null) return@onEach

            observePagedTestResults(
                params = ObservePagedTestResults.Params(
                    testId = test.id,
                    pagingConfig = PAGING_CONFIG
                )
            )
        }.launchIn(viewModelScope)
    }

    fun loadTest(testId: String) = launchWithLoader(loadingState) {
        getTest(params = GetTest.Params(testId = testId))
            .withErrorMessage(uiMessageManager)
            .onSuccess { test ->
                _test.value = test
            }.collect()
    }

    fun setIsTestEdited(isEdited: Boolean) {
        _isTestEdited.value = isEdited
    }

    fun deleteTest(onSuccess: () -> Unit) = launchWithLoader(loadingState) {
        _test.value.id?.let { testId ->
            deleteTest(params = DeleteTest.Params(testId = testId))
                .withErrorMessage(uiMessageManager)
                .onSuccess {
                    onSuccess()
                }.collect()
        }
    }

    fun showDialog(dialog: TeacherManageTestDialog) {
        _dialog.value = dialog
    }

    fun hideDialog() {
        _dialog.value = null
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id = id)
        }
    }
}