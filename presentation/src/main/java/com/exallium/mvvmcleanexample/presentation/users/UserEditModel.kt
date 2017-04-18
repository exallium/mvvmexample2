package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.actions.UseCaseAction
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import io.reactivex.Observable
import io.reactivex.ObservableSource

class UserEditModel(private val validateUserFirstNameUseCase: ValidateUserFirstNameUseCase,
                    private val validateUserLastNameUseCase: ValidateUserLastNameUseCase,
                    private val saveUserUseCase: SaveUserUseCase) {

    fun results(saveClicks: Observable<User>,
                firstNameChanges: Observable<String>,
                lastNameChanges: Observable<String>): Observable<UseCaseResult> {
        return Observable.merge(
                saveClicks.map(SaveUserUseCase::Action),
                firstNameChanges.map(ValidateUserFirstNameUseCase::Action),
                lastNameChanges.map(ValidateUserLastNameUseCase::Action)
        ).compose(this::applyUseCases)
    }

    private fun applyUseCases(upstream: Observable<UseCaseAction>): ObservableSource<UseCaseResult> {
        return Observable.merge(
                upstream.compose(validateUserFirstNameUseCase),
                upstream.compose(validateUserLastNameUseCase),
                upstream.compose(saveUserUseCase)
        )
    }
}