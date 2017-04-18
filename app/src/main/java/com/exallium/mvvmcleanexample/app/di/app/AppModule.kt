package com.exallium.mvvmcleanexample.app.di.app

import com.exallium.mvvmcleanexample.app.nav.AppRouter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideRouter() = AppRouter()
}

