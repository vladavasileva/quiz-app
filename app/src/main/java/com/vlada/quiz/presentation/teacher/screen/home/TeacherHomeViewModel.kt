/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.teacher.screen.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vlada.quiz.domain.model.Test
import com.vlada.quiz.domain.model.TestsSortOption
import com.vlada.quiz.domain.observer.ObservePagedTests
import com.vlada.quiz.presentation.util.ObservableLoadingCounter
import com.vlada.quiz.presentation.util.UiMessage
import com.vlada.quiz.presentation.util.UiMessageManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TeacherHomeViewModel(
    private val observePagedTests: ObservePagedTests
) : ViewModel() {

    private companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20
        )
    }

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val pagedTests: Flow<PagingData<Test>> = observePagedTests.flow.cachedIn(viewModelScope)

    private val selectedTestsSortOption: MutableStateFlow<TestsSortOption> =
        MutableStateFlow(TestsSortOption.DESCENDING)

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    val state: StateFlow<TeacherHomeViewState> = combine(
        searchQuery,
        selectedTestsSortOption,
        loadingState.observable,
        uiMessageManager.message
    ) { searchQuery, sortOption, isLoading, message ->
        TeacherHomeViewState(
            searchQuery = searchQuery,
            testsSortOption = sortOption,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TeacherHomeViewState.Empty,
    )

    init {
        combine(
            searchQuery
                .debounce(250),
            selectedTestsSortOption
        ) { searchQuery, selectedSortOption ->
            updatePagedTests(
                searchQuery = searchQuery.trim(),
                selectedTestsSortOption = selectedSortOption
            )
        }.launchIn(viewModelScope)
    }

    private fun updatePagedTests(
        searchQuery: String,
        selectedTestsSortOption: TestsSortOption,
        teacher: String? = "me"
    ) {
        observePagedTests(
            params = ObservePagedTests.Params(
                sort = selectedTestsSortOption,
                searchQuery = searchQuery,
                teacher = teacher,
                pagingConfig = PAGING_CONFIG
            )
        )
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSortOption(testsSortOption: TestsSortOption) {
        selectedTestsSortOption.value = testsSortOption
    }

    fun showMessage(@StringRes resId: Int, actionTag: String? = null) {
        viewModelScope.launch {
            uiMessageManager.emitMessage(UiMessage(resId = resId, actionTag = actionTag))
        }
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}