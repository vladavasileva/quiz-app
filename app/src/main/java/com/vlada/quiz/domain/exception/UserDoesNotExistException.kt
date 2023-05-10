/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.exception

import com.vlada.quiz.R

class UserDoesNotExistException : AppException(messageResId = R.string.error_user_does_not_exist)