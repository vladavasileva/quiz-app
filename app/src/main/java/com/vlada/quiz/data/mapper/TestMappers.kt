/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.mapper

import com.vlada.quiz.data.model.TestDto
import com.vlada.quiz.domain.model.Test

fun TestDto.toDomainModel() =
    Test(
        id = id,
        teacherId = teacherId,
        imageId = imageId,
        title = title,
        questions = questions.map {
            it.toDomainModel()
        },
        timestamp = timestamp
    )

fun Test.toDto() =
    TestDto(
        id = id,
        teacherId = teacherId,
        imageId = imageId,
        title = title,
        questions = questions.map {
            it.toDto()
        },
        timestamp = timestamp
    )