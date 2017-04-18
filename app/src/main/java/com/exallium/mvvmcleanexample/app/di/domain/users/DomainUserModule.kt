package com.exallium.mvvmcleanexample.app.di.domain.users

import com.exallium.mvvmcleanexample.app.di.PerScreen
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainUserModule {
    @Provides
    @PerScreen
    fun provideValidateUserFirstNameUseCase() = ValidateUserFirstNameUseCase()

    @Provides
    @PerScreen
    fun provideValidateUserLastNameUseCase() = ValidateUserLastNameUseCase()

    @Provides
    @PerScreen
    fun provideSaveUserUseCase(firstNameUseCase: ValidateUserFirstNameUseCase,
                               lastNameUseCase: ValidateUserLastNameUseCase)
            = SaveUserUseCase(firstNameUseCase, lastNameUseCase)
}