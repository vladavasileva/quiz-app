/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteImage(
    private val imageRepository: ImageRepository
) : Interactor<DeleteImage.Params, Unit>() {

    override suspend fun doWork(params: Params) = withContext(Dispatchers.IO) {
        imageRepository.deleteImage(imageId = params.imageId)
    }

    data class Params(val imageId: String)
}