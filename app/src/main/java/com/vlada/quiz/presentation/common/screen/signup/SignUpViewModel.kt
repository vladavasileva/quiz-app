/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.R
import com.vlada.quiz.domain.interactor.SignUp
import com.vlada.quiz.domain.interactor.ValidateEmail
import com.vlada.quiz.domain.interactor.ValidatePassword
import com.vlada.quiz.domain.model.Credential
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUp: SignUp,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _email: MutableStateFlow<TextFieldState> = MutableStateFlow(TextFieldState.Empty)
    private val _password: MutableStateFlow<TextFieldState> = MutableStateFlow(TextFieldState.Empty)

    val state: StateFlow<SignUpViewState> = combine(
        _email,
        _password,
        loadingState.observable,
        uiMessageManager.message
    ) { email, password, isLoading, message ->
        SignUpViewState(
            email = email,
            password = password,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SignUpViewState.Empty,
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

    fun signUp() = launchWithLoader(loadingState) {
        val email = _email.value.text.trim()
        val password = _password.value.text.trim()

        val isEmailEmpty = email.isEmpty()

        val isEmailInvalid = !validateEmail
            .executeSync(params = ValidateEmail.Params(email = email))

        val isPasswordEmpty = password.isEmpty()

        val isPasswordInvalid = !validatePassword
            .executeSync(params = ValidatePassword.Params(password = password))

        _email.update { state ->
            val resId = if (isEmailEmpty) {
                R.string.error_empty_email
            } else if (isEmailInvalid) {
                R.string.error_invalid_email_format
            } else {
                null
            }

            state.copy(errorResId = resId)
        }

        _password.update { state ->
            val resId = if(isPasswordEmpty) {
                R.string.error_empty_password
            } else if(isPasswordInvalid) {
                R.string.error_invalid_password_format
            } else {
                null
            }

            state.copy(errorResId = resId)
        }

        if (isEmailInvalid || isPasswordInvalid || isEmailEmpty || isPasswordEmpty) {
            return@launchWithLoader
        }

        val credential = Credential(
            email = email,
            password = password
        )

        signUp(params = SignUp.Params(credential = credential))
            .withErrorMessage(uiMessageManager)
            .collect()
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}