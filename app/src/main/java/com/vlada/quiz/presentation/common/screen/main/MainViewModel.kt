/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.GetUserDetails
import com.vlada.quiz.domain.model.UserAuthState
import com.vlada.quiz.domain.model.UserRole
import com.vlada.quiz.domain.observer.ObserveUserAuthState
import com.vlada.quiz.presentation.common.navigation.Screen
import com.vlada.quiz.presentation.util.onSuccess
import kotlinx.coroutines.flow.*

class MainViewModel(
    observeUserAuthState: ObserveUserAuthState,
    private val getUserDetails: GetUserDetails
) : ViewModel() {

    private val _event: MutableStateFlow<MainEvent?> = MutableStateFlow(null)

    val state: StateFlow<MainViewState> = combine(
        _event
    ) { event ->
        MainViewState(
            event = event.first()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = MainViewState.Empty,
    )

    init {
        observeUserAuthState.flow.onEach { userAuthState ->
            when (userAuthState) {
                UserAuthState.LoggedIn -> {
                    getUserDetails(Unit)
                        .onSuccess { userDetails ->
                            val screen = if (userDetails.role == UserRole.TEACHER) {
                                Screen.TEACHER_HOME
                            } else {
                                Screen.STUDENT_HOME
                            }
                            _event.value = MainEvent.Navigate(screen = screen)
                        }.collect()
                }

                UserAuthState.LoggedOut -> {
                    _event.value = MainEvent.Navigate(screen = Screen.LOG_IN)
                }

                UserAuthState.UserDetailsRequired -> {
                    _event.value = MainEvent.Navigate(screen = Screen.USER_DETAILS)
                }
            }
        }.launchIn(viewModelScope)

        observeUserAuthState(Unit)
    }

    fun clearEvent() {
        _event.value = null
    }
}