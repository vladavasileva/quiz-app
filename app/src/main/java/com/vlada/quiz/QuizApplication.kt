/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz


import android.app.Application
import com.vlada.quiz.data.dataModule
import com.vlada.quiz.domain.domainModule
import com.vlada.quiz.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}