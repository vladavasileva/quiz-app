/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.observer

import com.vlada.quiz.domain.SubjectInteractor
import com.vlada.quiz.domain.model.UserAuthState
import com.vlada.quiz.domain.repository.UserAuthRepository
import com.vlada.quiz.domain.repository.UserDetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class ObserveUserAuthState(
    private val authRepository: UserAuthRepository,
    private val userDetailsRepository: UserDetailsRepository
) : SubjectInteractor<Unit, UserAuthState>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createObservable(params: Unit): Flow<UserAuthState> {
        return authRepository.observeUserId()
            .flatMapLatest { userId ->
                when (userId) {
                    null -> flowOf(UserAuthState.LoggedOut)
                    else -> userDetailsRepository.observeUserDetails(userId).map { userDetails ->
                        if (userDetails == null) {
                            UserAuthState.UserDetailsRequired
                        } else {
                            UserAuthState.LoggedIn
                        }
                    }
                }
            }
    }
}