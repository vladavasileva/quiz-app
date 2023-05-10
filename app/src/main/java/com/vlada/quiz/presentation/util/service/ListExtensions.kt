/*
 * Copyright (c) 2023. Vladyslava Vasylieva
 */

package com.vlada.quiz.presentation.util.service

fun <T> Iterable<T>.nextElement(element: T): T? {
    val currentIndex = this.indexOf(element)
    if(currentIndex == -1) return null
    return this.elementAtOrNull(currentIndex + 1)
}