/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.user_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.R
import com.vlada.quiz.domain.interactor.LogOut
import com.vlada.quiz.domain.interactor.SaveUserDetails
import com.vlada.quiz.domain.interactor.ValidateName
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.model.UserRole
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val saveUserDetails: SaveUserDetails,
    private val logOut: LogOut,
    private val validateName: ValidateName
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _givenName: MutableStateFlow<TextFieldState> =
        MutableStateFlow(TextFieldState.Empty)

    private val _familyName: MutableStateFlow<TextFieldState> =
        MutableStateFlow(TextFieldState.Empty)

    private val _userRole: MutableStateFlow<UserRole> =
        MutableStateFlow(UserRole.STUDENT)

    val state: StateFlow<UserDetailsViewState> = combine(
        _givenName,
        _familyName,
        _userRole,
        loadingState.observable,
        uiMessageManager.message
    ) { givenName, familyName, userRole, isLoading, message ->
        UserDetailsViewState(
            givenName = givenName,
            familyName = familyName,
            userRole = userRole,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UserDetailsViewState.Empty,
    )

    fun setGivenName(name: String) {
        _givenName.update {
            it.copy(
                text = name,
                errorResId = null
            )
        }
    }

    fun setFamilyName(name: String) {
        _familyName.update {
            it.copy(
                text = name,
                errorResId = null
            )
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun logOut() = launchWithLoader(loadingState) {
        logOut(Unit).collect()
    }

    fun saveUserDetails() = launchWithLoader(loadingState) {
        val givenName = _givenName.value.text.trim()
        val familyName = _familyName.value.text.trim()

        val isGivenNameInvalid = !validateName
            .executeSync(params = ValidateName.Params(name = givenName))

        val isFamilyNameInvalid = !validateName
            .executeSync(params = ValidateName.Params(name = familyName))

        if (isGivenNameInvalid) {
            _givenName.update { state ->
                state.copy(errorResId = R.string.error_empty_given_name)
            }
        }

        if (isFamilyNameInvalid) {
            _familyName.update { state ->
                state.copy(errorResId = R.string.error_empty_family_name)
            }
        }

        if (isGivenNameInvalid || isFamilyNameInvalid) {
            return@launchWithLoader
        }

        val userDetails = UserDetails(
            role = _userRole.value,
            givenName = givenName,
            familyName = familyName
        )

        saveUserDetails(params = SaveUserDetails.Params(userDetails = userDetails))
            .withErrorMessage(uiMessageManager)
            .collect()
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}