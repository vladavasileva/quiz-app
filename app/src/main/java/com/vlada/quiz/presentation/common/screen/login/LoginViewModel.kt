/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.R
import com.vlada.quiz.domain.interactor.LogIn
import com.vlada.quiz.domain.model.Credential
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val logIn: LogIn
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _email: MutableStateFlow<TextFieldState> = MutableStateFlow(TextFieldState.Empty)
    private val _password: MutableStateFlow<TextFieldState> = MutableStateFlow(TextFieldState.Empty)

    val state: StateFlow<LoginViewState> = combine(
        _email,
        _password,
        loadingState.observable,
        uiMessageManager.message
    ) { email, password, isLoading, message ->
        LoginViewState(
            email = email,
            password = password,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = LoginViewState.Empty,
    )

    fun setEmail(email: String) {
        _email.update {
            it.copy(
                text = email,
                errorResId = null
            )
        }
    }

    fun setPassword(password: String) {
        _password.update {
            it.copy(
                text = password,
                errorResId = null
            )
        }
    }

    fun logIn() = launchWithLoader(loadingState) {
        val email = _email.value.text.trim()
        val password = _password.value.text.trim()

        val isEmailInvalid = email.isEmpty()
        val isPasswordInvalid = password.isEmpty()

        if (isEmailInvalid) {
            _email.update { state ->
                state.copy(errorResId = R.string.error_empty_email)
            }
        }

        if (isPasswordInvalid) {
            _password.update { state ->
                state.copy(errorResId = R.string.error_empty_password)
            }
        }

        if (isEmailInvalid || isPasswordInvalid) {
            return@launchWithLoader
        }

        val credential = Credential(
            email = email,
            password = password
        )

        logIn(params = LogIn.Params(credential = credential))
            .withErrorMessage(uiMessageManager)
            .collect()
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}