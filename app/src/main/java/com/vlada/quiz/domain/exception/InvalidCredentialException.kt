/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.exception

import com.vlada.quiz.R

class InvalidCredentialException : AppException(messageResId = R.string.error_invalid_credential)