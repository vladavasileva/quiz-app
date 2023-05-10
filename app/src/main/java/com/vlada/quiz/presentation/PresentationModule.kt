/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation

import com.vlada.quiz.presentation.common.screen.login.LoginViewModel
import com.vlada.quiz.presentation.common.screen.main.MainViewModel
import com.vlada.quiz.presentation.common.screen.signup.SignUpViewModel
import com.vlada.quiz.presentation.common.screen.user_details.UserDetailsViewModel
import com.vlada.quiz.presentation.student.screen.home.StudentHomeViewModel
import com.vlada.quiz.presentation.student.screen.profile.StudentProfileViewModel
import com.vlada.quiz.presentation.student.screen.test.StudentTestViewModel
import com.vlada.quiz.presentation.student.screen.test_details.StudentTestDetailsViewModel
import com.vlada.quiz.presentation.teacher.screen.edit_test.TeacherEditTestViewModel
import com.vlada.quiz.presentation.teacher.screen.home.TeacherHomeViewModel
import com.vlada.quiz.presentation.teacher.screen.manage_test.TeacherManageTestViewModel
import com.vlada.quiz.presentation.teacher.screen.profile.TeacherProfileViewModel
import com.vlada.quiz.presentation.util.service.ImageService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single {
        ImageService()
    }

    viewModel {
        MainViewModel(get(), get())
    }

    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        SignUpViewModel(get(), get(), get())
    }

    viewModel {
        UserDetailsViewModel(get(), get(), get())
    }

    viewModel {
        TeacherHomeViewModel(get())
    }

    viewModel {
        TeacherManageTestViewModel(get(), get(), get())
    }

    viewModel {
        TeacherEditTestViewModel(get(), get(), get(), get())
    }

    viewModel {
        TeacherProfileViewModel(get(), get(), get(), get())
    }

    viewModel {
        StudentHomeViewModel(get())
    }

    viewModel {
        StudentProfileViewModel(get(), get(), get(), get())
    }

    viewModel {
        StudentTestDetailsViewModel(get(), get())
    }

    viewModel {
        StudentTestViewModel(get(), get())
    }
}