/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.model.AnswerOption
import com.vlada.quiz.domain.model.Test

class ValidateTest : Interactor<ValidateTest.Params, ValidateTest.Result>() {
    override suspend fun doWork(params: Params): Result {
        val errors = mutableSetOf<ValidationError>()

        if (params.test.title.isEmpty()) {
            errors += ValidationError.EmptyTitle
        }

        params.test.questions.forEachIndexed { index, question ->
            if (question.question.isEmpty()) {
                errors += ValidationError.EmptyQuestion(questionIndex = index)
            }

            val answers = listOf(
                question.answer1 to AnswerOption.A1,
                question.answer2 to AnswerOption.A2,
                question.answer3 to AnswerOption.A3,
                question.answer4 to AnswerOption.A4
            )

            for ((answer, answerOption) in answers) {
                if (answer.isEmpty()) {
                    errors += ValidationError.EmptyAnswer(
                        questionIndex = index,
                        answer = answerOption
                    )
                }
            }
        }

        return if (errors.isEmpty()) {
            Result.Valid
        } else {
            Result.Error(errors = errors)
        }
    }

    data class Params(val test: Test)

    sealed class Result {
        object Valid : Result()
        class Error(val errors: Set<ValidationError>) : Result()
    }

    sealed class ValidationError {
        object EmptyTitle : ValidationError()
        class EmptyQuestion(val questionIndex: Int) : ValidationError()
        class EmptyAnswer(val questionIndex: Int, val answer: AnswerOption) : ValidationError()
    }
}