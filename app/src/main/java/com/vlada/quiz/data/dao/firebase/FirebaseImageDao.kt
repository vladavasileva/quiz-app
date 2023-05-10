/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.dao.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vlada.quiz.data.dao.ImageDao
import kotlinx.coroutines.tasks.await


class FirebaseImageDao : ImageDao {
    private val storageRef = Firebase.storage.reference

    override suspend fun uploadImage(image: ByteArray, imageId: String) {
        storageRef.child("images/${imageId}.jpg").putBytes(image).await()
    }

    override suspend fun deleteImage(imageId: String) {
        storageRef.child("images/${imageId}.jpg").delete()
    }

    override suspend fun getImageLink(imageId: String): String {
        return storageRef.child("images/${imageId}.jpg").downloadUrl.await().toString()
    }
}