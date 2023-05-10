/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class FirestorePagedResult<out T>(
    val data: T,
    val lastDocument: DocumentSnapshot?
)
