/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.mapper

import com.vlada.quiz.data.model.QuestionDto
import com.vlada.quiz.domain.model.Question

fun QuestionDto.toDomainModel() =
    Question(
        id = id,
        imageId = imageId,
        question = question,
        answer1 = answer1,
        answer2 = answer2,
        answer3 = answer3,
        answer4 = answer4,
        correctAnswer = correctAnswer
    )

fun Question.toDto() =
    QuestionDto(
        id = id,
        imageId = imageId,
        question = question,
        answer1 = answer1,
        answer2 = answer2,
        answer3 = answer3,
        answer4 = answer4,
        correctAnswer = correctAnswer
    )