package com.exallium.mvvmcleanexample.app.users.di

import com.exallium.mvvmcleanexample.app.di.PerScreen
import com.exallium.mvvmcleanexample.app.di.app.AppComponent
import com.exallium.mvvmcleanexample.app.users.UserEditView
import dagger.Component

@PerScreen
@Component(modules = arrayOf(UserEditModule::class), dependencies = arrayOf(AppComponent::class))
interface UserEditComponent {
    fun inject(userEditView: UserEditView)
}