/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.exception

import androidx.annotation.StringRes

open class AppException(@StringRes val messageResId: Int) : Exception()