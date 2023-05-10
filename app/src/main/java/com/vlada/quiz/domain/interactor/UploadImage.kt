/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadImage(
    private val imageRepository: ImageRepository
) : Interactor<UploadImage.Params, Unit>() {

    override suspend fun doWork(params: Params) = withContext(Dispatchers.IO) {
        imageRepository.uploadImage(image = params.image, imageId = params.imageId)
    }

    @Suppress("ArrayInDataClass")
    data class Params(val image: ByteArray, val imageId: String)
}