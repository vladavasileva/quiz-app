/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.repository

interface ImageRepository {
    suspend fun uploadImage(image: ByteArray, imageId: String)

    suspend fun deleteImage(imageId: String)

    suspend fun getImageLink(imageId: String): String
}