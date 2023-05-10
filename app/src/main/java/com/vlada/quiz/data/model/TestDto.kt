/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class TestDto(
    @DocumentId
    val id: String? = null,
    val teacherId: String? = null,
    val imageId: String? = null,
    val title: String = "",
    val questions: List<QuestionDto> = emptyList(),
    val timestamp: Long = 0L,
    val searchTitle: String = ""
)