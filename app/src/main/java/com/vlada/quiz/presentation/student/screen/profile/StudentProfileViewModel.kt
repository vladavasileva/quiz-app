/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.student.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlada.quiz.R
import com.vlada.quiz.domain.InvokeStatus
import com.vlada.quiz.domain.interactor.GetUserDetails
import com.vlada.quiz.domain.interactor.LogOut
import com.vlada.quiz.domain.interactor.SaveUserDetails
import com.vlada.quiz.domain.interactor.ValidateName
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.model.UserRole
import com.vlada.quiz.presentation.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudentProfileViewModel(
    private val logOut: LogOut,
    getUserDetails: GetUserDetails,
    private val saveUserDetails: SaveUserDetails,
    private val validateName: ValidateName
) : ViewModel() {

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _initialGivenName: MutableStateFlow<String> =
        MutableStateFlow("")

    private val _initialFamilyName: MutableStateFlow<String> =
        MutableStateFlow("")

    private val _givenName: MutableStateFlow<TextFieldState> =
        MutableStateFlow(TextFieldState.Empty)

    private val _familyName: MutableStateFlow<TextFieldState> =
        MutableStateFlow(TextFieldState.Empty)

    private val _isSaveButtonVisible = combine(
        _givenName,
        _familyName,
        _initialGivenName,
        _initialFamilyName
    ) { givenName, familyName, initialGivenName, initialFamilyName ->
        givenName.text != initialGivenName || familyName.text != initialFamilyName
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = false,
    )

    val state: StateFlow<StudentProfileViewState> = combine(
        _givenName,
        _familyName,
        _isSaveButtonVisible,
        loadingState.observable,
        uiMessageManager.message
    ) { givenName, familyName, isSaveButtonVisible, isLoading, message ->
        StudentProfileViewState(
            givenName = givenName,
            familyName = familyName,
            isSaveButtonVisible = isSaveButtonVisible,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = StudentProfileViewState.Empty,
    )

    init {
        getUserDetails(Unit).withLoader(loadingState)
            .withErrorMessage(uiMessageManager)
            .onSuccess { userDetails ->
                _initialGivenName.value = userDetails.givenName
                _initialFamilyName.value = userDetails.familyName

                _givenName.value = TextFieldState(
                    text = userDetails.givenName
                )

                _familyName.value = TextFieldState(
                    text = userDetails.familyName
                )
            }.launchIn(viewModelScope)
    }

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
            role = UserRole.STUDENT,
            givenName = givenName,
            familyName = familyName
        )

        saveUserDetails(params = SaveUserDetails.Params(userDetails = userDetails))
            .withErrorMessage(uiMessageManager)
            .onSuccess {
                _initialGivenName.value = givenName
                _initialFamilyName.value = familyName
            }.collect()
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}