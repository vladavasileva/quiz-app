/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.main

import com.vlada.quiz.presentation.common.navigation.Screen

sealed class MainEvent {
    class Navigate(val screen: Screen) : MainEvent()
}
