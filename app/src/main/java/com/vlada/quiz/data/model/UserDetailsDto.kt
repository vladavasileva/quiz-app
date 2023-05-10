/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.model

import com.vlada.quiz.domain.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsDto(
    val userId: String? = null,
    val role: String = UserRole.TEACHER.name,
    val givenName: String = "",
    val familyName: String = ""
)