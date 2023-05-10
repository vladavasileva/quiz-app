/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.screen.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.vlada.quiz.domain.interactor.*
import com.vlada.quiz.presentation.common.navigation.AppNavHost
import com.vlada.quiz.presentation.theme.QuizTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity(), KoinComponent {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizTheme(isDynamicColor = true) {
                Surface {
                    val viewModel: MainViewModel = koinViewModel()
                    val viewState by viewModel.state.collectAsState()
                    val navController = rememberAnimatedNavController()

                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavHost(navController = navController)
                    }

                    viewState.event?.let { event ->
                        when (event) {
                            is MainEvent.Navigate -> {
                                navController.navigate(event.screen.route) {
                                    popUpTo(0)
                                }
                            }
                        }
                        viewModel.clearEvent()
                    }
                }
            }
        }
    }
}