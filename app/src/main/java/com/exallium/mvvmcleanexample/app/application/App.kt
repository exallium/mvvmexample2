package com.exallium.mvvmcleanexample.app.application

import android.app.Application
import com.exallium.mvvmcleanexample.app.di.app.AppComponent
import com.exallium.mvvmcleanexample.app.di.app.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        createComponent()
    }

    fun createComponent() {
        component = DaggerAppComponent.create()
    }

}