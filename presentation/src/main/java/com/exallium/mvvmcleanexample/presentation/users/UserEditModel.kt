package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import io.reactivex.Observable

class UserEditModel(private val validateUserFirstNameUseCase: ValidateUserFirstNameUseCase,
                    private val validateUserLastNameUseCase: ValidateUserLastNameUseCase,
                    private val saveUserUseCase: SaveUserUseCase) {
    fun results(saveClicks: Observable<User>,
                firstNameChanges: Observable<String>,
                lastNameChanges: Observable<String>): Observable<UseCaseResult> {
        return Observable.merge(
                saveClicks.compose(saveUserUseCase),
                firstNameChanges.compose(validateUserFirstNameUseCase),
                lastNameChanges.compose(validateUserLastNameUseCase))
    }
}