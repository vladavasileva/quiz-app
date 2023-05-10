/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.data

import com.vlada.quiz.data.dao.*
import com.vlada.quiz.data.dao.firebase.*
import com.vlada.quiz.data.dao.local.LocalUserDetailsDao
import com.vlada.quiz.data.repository.*
import com.vlada.quiz.domain.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {
    single<UserDetailsDao>(named("remote")) {
        FirebaseUserDetailsDao()
    }

    single<UserDetailsDao>(named("local")) {
        LocalUserDetailsDao(androidContext())
    }

    single<UserAuthDao> {
        FirebaseUserAuthDao()
    }

    single<UserDetailsRepository> {
        UserDetailsRepositoryImpl(get(named("remote")), get(named("local")))
    }

    single<UserAuthRepository> {
        UserAuthRepositoryImpl(get())
    }

    single<ImageDao> {
        FirebaseImageDao()
    }

    single<ImageRepository> {
        ImageRepositoryImpl(get())
    }

    single<TestDao> {
        FirebaseTestDao()
    }

    single<TestRepository> {
        TestRepositoryImpl(get())
    }

    single<TestResultDao> {
        FirebaseTestResultDao()
    }

    single<TestRepository> {
        TestRepositoryImpl(get())
    }

    single<TestResultRepository> {
        TestResultRepositoryImpl(get(), get())
    }
}