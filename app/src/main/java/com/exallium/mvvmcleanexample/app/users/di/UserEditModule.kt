package com.exallium.mvvmcleanexample.app.users.di

import com.exallium.mvvmcleanexample.app.di.PerScreen
import com.exallium.mvvmcleanexample.app.di.domain.users.DomainUserModule
import com.exallium.mvvmcleanexample.app.nav.AppRouter
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.users.UserEditModel
import com.exallium.mvvmcleanexample.presentation.users.UserEditViewModel
import dagger.Module
import dagger.Provides

@Module(includes=arrayOf(DomainUserModule::class))
class UserEditModule {

    @Provides
    @PerScreen
    fun provideUserEditViewModel(userEditModel: UserEditModel, router: AppRouter) = UserEditViewModel(userEditModel, router)

    @Provides
    @PerScreen
    fun provideUserEditModel(saveUserUseCase: SaveUserUseCase,
                             validateUserFirstNameUseCase: ValidateUserFirstNameUseCase,
                             validateUserLastNameUseCase: ValidateUserLastNameUseCase)
        = UserEditModel(validateUserFirstNameUseCase, validateUserLastNameUseCase, saveUserUseCase)

}