package com.exallium.mvvmcleanexample.app.di.app

import com.exallium.mvvmcleanexample.app.nav.AppRouter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun appRouter(): AppRouter
}