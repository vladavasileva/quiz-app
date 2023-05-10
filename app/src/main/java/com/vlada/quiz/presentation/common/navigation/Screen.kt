/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.common.navigation

enum class Screen(val route: String) {
    SPLASH(route = "splash"),
    LOG_IN(route = "log_in"),
    SIGN_UP(route = "sign_up"),
    USER_DETAILS(route = "user_details"),
    TEACHER_HOME(route = "teacher_home"),
    TEACHER_MANAGE_TEST(route = "teacher_manage_test"),
    TEACHER_EDIT_TEST(route = "teacher_edit_test"),
    TEACHER_PROFILE(route = "teacher_profile"),
    STUDENT_HOME(route = "student_home"),
    STUDENT_PROFILE(route = "student_profile"),
    STUDENT_TEST_DETAILS(route = "student_test_details"),
    STUDENT_TEST(route = "student_test")
}