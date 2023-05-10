/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val userId: String? = null,
    val role: UserRole = UserRole.TEACHER,
    val givenName: String = "",
    val familyName: String = ""
) {
    companion object {
        val Empty = UserDetails()
    }
}