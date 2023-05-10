/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.model

data class Test(
    val id: String? = null,
    val teacherId: String? = null,
    val imageId: String? = null,
    val title: String = "",
    val questions: List<Question> = emptyList(),
    val timestamp: Long = 0L
) {
    companion object {
        val Empty = Test()
    }
}