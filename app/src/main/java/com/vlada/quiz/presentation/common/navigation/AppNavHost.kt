/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.vlada.quiz.presentation.common.screen.login.Login
import com.vlada.quiz.presentation.common.screen.signup.SignUp
import com.vlada.quiz.presentation.common.screen.splash.Splash
import com.vlada.quiz.presentation.common.screen.user_details.UserDetails
import com.vlada.quiz.presentation.student.screen.home.StudentHome
import com.vlada.quiz.presentation.student.screen.profile.StudentProfile
import com.vlada.quiz.presentation.student.screen.test.StudentTest
import com.vlada.quiz.presentation.student.screen.test_details.StudentTestDetails
import com.vlada.quiz.presentation.teacher.screen.edit_test.TeacherEditTest
import com.vlada.quiz.presentation.teacher.screen.home.TeacherHome
import com.vlada.quiz.presentation.teacher.screen.home.TeacherHomeNavBackTestResult
import com.vlada.quiz.presentation.teacher.screen.manage_test.TeacherManageTest
import com.vlada.quiz.presentation.teacher.screen.profile.TeacherProfile

private val fadeAnimationSpec = tween<Float>(300)
private val slideAnimationSpec = tween<IntOffset>(300)

private val slideInFromLeft =
    slideInHorizontally(animationSpec = slideAnimationSpec, initialOffsetX = { -it })
private val slideInFromRight =
    slideInHorizontally(animationSpec = slideAnimationSpec, initialOffsetX = { it })
private val slideOutFromLeft =
    slideOutHorizontally(animationSpec = slideAnimationSpec, targetOffsetX = { -it })
private val slideOutFromRight =
    slideOutHorizontally(animationSpec = slideAnimationSpec, targetOffsetX = { it })

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.SPLASH.route,
        enterTransition = {
            fadeIn(animationSpec = fadeAnimationSpec)
        },
        exitTransition = {
            fadeOut(animationSpec = fadeAnimationSpec)
        }
    ) {
        composable(
            route = Screen.SPLASH.route,
            exitTransition = {
                scaleOut(targetScale = 0.9f)
            }
        ) {
            Splash()
        }
        composable(
            route = Screen.LOG_IN.route,
            enterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) }
        ) {
            Login(navController = navController)
        }
        composable(
            route = Screen.SIGN_UP.route,
            enterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) }
        ) {
            SignUp(navController = navController)
        }
        composable(
            route = Screen.USER_DETAILS.route,
            enterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) }
        ) {
            UserDetails()
        }

        composable(
            route = Screen.TEACHER_HOME.route,
            enterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
            exitTransition = { slideOutFromLeft },
            popEnterTransition = { slideInFromLeft }
        ) { navBackStackEntry ->
            val testResult = navBackStackEntry.savedStateHandle.get<String>("navBackTestResult")
                ?.let { result ->
                    try {
                        TeacherHomeNavBackTestResult.valueOf(result)
                    } catch (_: Exception) {
                        null
                    }
                }

            navBackStackEntry.savedStateHandle.remove<String>("navBackTestResult")

            TeacherHome(
                navBackTestResult = testResult,
                navController = navController
            )
        }
        composable(
            route = Screen.TEACHER_MANAGE_TEST.route + "/{testId}",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutFromLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutFromRight }
        ) { navBackStackEntry ->
            val testId = navBackStackEntry.arguments?.getString("testId") ?: return@composable

            TeacherManageTest(
                testId = testId,
                navController = navController
            )
        }
        composable(
            route = Screen.TEACHER_EDIT_TEST.route + "?testId={testId}",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutFromRight }
        ) { navBackStackEntry ->
            TeacherEditTest(
                testId = navBackStackEntry.arguments?.getString("testId"),
                navController = navController
            )
        }
        composable(
            route = Screen.TEACHER_PROFILE.route,
            enterTransition = { slideInFromRight },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) },
            popExitTransition = { slideOutFromRight }
        ) {
            TeacherProfile(navController = navController)
        }

        composable(
            route = Screen.STUDENT_HOME.route,
            enterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
            exitTransition = { slideOutFromLeft },
            popEnterTransition = { slideInFromLeft }
        ) {
            StudentHome(navController = navController)
        }
        composable(
            route = Screen.STUDENT_TEST_DETAILS.route + "/{testId}",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutFromLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutFromRight }
        ) { navBackStackEntry ->
            val testId = navBackStackEntry.arguments?.getString("testId") ?: return@composable

            StudentTestDetails(
                testId = testId,
                navController = navController
            )
        }
        composable(
            route = Screen.STUDENT_TEST.route + "/{testId}",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutFromRight }
        ) { navBackStackEntry ->
            val testId = navBackStackEntry.arguments?.getString("testId") ?: return@composable

            StudentTest(
                testId = testId,
                navController = navController
            )
        }
        composable(
            route = Screen.STUDENT_PROFILE.route,
            enterTransition = { slideInFromRight },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) },
            popExitTransition = { slideOutFromRight }
        ) {
            StudentProfile(navController = navController)
        }
    }
}