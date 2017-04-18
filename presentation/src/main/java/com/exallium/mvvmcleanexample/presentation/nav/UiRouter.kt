package com.exallium.mvvmcleanexample.presentation.nav

interface UiRouter {
    fun goBack()
    fun displayMessage(message: String)
}