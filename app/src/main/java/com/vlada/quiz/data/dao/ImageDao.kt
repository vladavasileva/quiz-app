/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao

interface ImageDao {
    suspend fun uploadImage(image: ByteArray, imageId: String)

    suspend fun deleteImage(imageId: String)

    suspend fun getImageLink(imageId: String): String
}