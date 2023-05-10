/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

sealed class UserAuthState {
    object LoggedIn : UserAuthState()
    object LoggedOut : UserAuthState()
    object UserDetailsRequired : UserAuthState()
}