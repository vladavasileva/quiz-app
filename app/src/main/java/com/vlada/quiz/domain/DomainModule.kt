/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.domain

import com.vlada.quiz.domain.interactor.*
import com.vlada.quiz.domain.observer.ObservePagedTestResults
import com.vlada.quiz.domain.observer.ObservePagedTests
import com.vlada.quiz.domain.observer.ObserveUserAuthState
import org.koin.dsl.module

val domainModule = module {
    single {
        DeleteImage(get())
    }

    single {
        DeleteTest(get())
    }

    single {
        GetImageLink(get())
    }

    single {
        GetTest(get())
    }

    single {
        GetTestResult(get(), get())
    }

    single {
        GetUserDetails(get(), get())
    }

    single {
        LogIn(get())
    }

    single {
        LogOut(get())
    }

    single {
        SaveUserDetails(get(), get())
    }

    single {
        SaveTest(get(), get())
    }

    single {
        SaveTestResult(get(), get())
    }

    single {
        SignUp(get())
    }

    single {
        UploadImage(get())
    }

    single {
        ValidateEmail()
    }

    single {
        ValidateName()
    }

    single {
        ValidatePassword()
    }

    single {
        ValidateTest()
    }

    single {
        ObserveUserAuthState(get(), get())
    }

    single {
        ObservePagedTests(get(), get())
    }

    single {
        ObservePagedTestResults(get())
    }
}