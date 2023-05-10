/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.mapper

import com.vlada.quiz.data.model.UserDetailsDto
import com.vlada.quiz.domain.model.UserDetails
import com.vlada.quiz.domain.model.UserRole

fun UserDetailsDto.toDomainModel() =
    UserDetails(
        userId = userId,
        role = UserRole.valueOf(role),
        givenName = givenName,
        familyName = familyName
    )

fun UserDetails.toDto() =
    UserDetailsDto(
        userId = userId,
        role = role.name,
        givenName = givenName,
        familyName = familyName
    )