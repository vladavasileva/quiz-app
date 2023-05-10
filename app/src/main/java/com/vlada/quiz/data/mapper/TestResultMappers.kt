/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.mapper

import com.vlada.quiz.data.model.TestResultDto
import com.vlada.quiz.domain.model.QuestionAnswer
import com.vlada.quiz.domain.model.TestResult
import com.vlada.quiz.domain.model.UserDetails

fun TestResultDto.toDomainModel(userDetails: UserDetails) =
    TestResult(
        testId = testId,
        userDetails = userDetails,
        answers = answers.map {
            QuestionAnswer(
                questionId = it.key,
                answer = it.value
            )
        }
    )

fun TestResult.toDto() =
    TestResultDto(
        testId = testId,
        userId = userDetails.userId,
        answers = answers.associate {
            it.questionId to it.answer
        }
    )