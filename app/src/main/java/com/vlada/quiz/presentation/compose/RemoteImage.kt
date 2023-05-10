/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.vlada.quiz.domain.interactor.GetImageLink
import com.vlada.quiz.presentation.util.onSuccess
import kotlinx.coroutines.flow.collect
import org.koin.androidx.compose.get

@Composable
fun RemoteImage(
    modifier: Modifier = Modifier,
    imageId: String
) {
    val getImageLink: GetImageLink = get()

    var url by remember { mutableStateOf<String?>(null) }

    val painter: AsyncImagePainter = rememberAsyncImagePainter(
        model = url
    )

    LaunchedEffect(imageId) {
        getImageLink(params = GetImageLink.Params(imageId = imageId))
            .onSuccess {
                url = it
            }.collect()
    }

    Box(
        modifier = Modifier
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = modifier,
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        if (painter.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator()
        }
    }
}