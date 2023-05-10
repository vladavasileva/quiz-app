/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data.repository

import android.util.Log
import com.vlada.quiz.data.dao.ImageDao
import com.vlada.quiz.domain.exception.FailedToUploadTheImageException
import com.vlada.quiz.domain.exception.UnexpectedException
import com.vlada.quiz.domain.repository.ImageRepository

class ImageRepositoryImpl(
    private val imageDao: ImageDao,
) : ImageRepository {

    private val TAG = ImageRepositoryImpl::class.java.simpleName

    override suspend fun uploadImage(image: ByteArray, imageId: String) {
        try {
            imageDao.uploadImage(image = image, imageId = imageId)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw FailedToUploadTheImageException()
        }
    }

    override suspend fun deleteImage(imageId: String) {
        try {
            imageDao.deleteImage(imageId = imageId)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }

    override suspend fun getImageLink(imageId: String): String {
        return try {
            imageDao.getImageLink(imageId = imageId)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            throw UnexpectedException()
        }
    }
}