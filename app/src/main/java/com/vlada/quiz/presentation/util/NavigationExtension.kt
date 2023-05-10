/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.util

import androidx.navigation.NavController

fun NavController.popBackStackIfNotLast(): Boolean {
    return if(this.backQueue.count { it.destination.route != null } > 1) {
        this.popBackStack()
    } else {
        false
    }
}