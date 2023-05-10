/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain.interactor

import com.vlada.quiz.domain.Interactor
import com.vlada.quiz.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetImageLink(
    private val imageRepository: ImageRepository
) : Interactor<GetImageLink.Params, String>() {

    override suspend fun doWork(params: Params): String = withContext(Dispatchers.IO) {
        return@withContext imageRepository.getImageLink(imageId = params.imageId)
    }

    @Suppress("ArrayInDataClass")
    data class Params(val imageId: String)
}