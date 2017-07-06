package com.exallium.mvvmcleanexample.app.application

import android.app.Application
import com.exallium.mvvmcleanexample.BR
import com.exallium.mvvmcleanexample.app.di.app.AppComponent
import com.exallium.mvvmcleanexample.app.di.app.DaggerAppComponent
import com.exallium.mvvmcleanexample.presentation.utils.PropertyCacher

class App : Application() {

    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        createComponent()
        initDataBinding()
    }

    fun createComponent() {
        component = DaggerAppComponent.create()
    }

    private fun initDataBinding() {
        PropertyCacher.bindableResourceClass = BR::class.java
    }

}