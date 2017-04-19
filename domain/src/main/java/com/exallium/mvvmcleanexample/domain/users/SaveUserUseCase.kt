package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class SaveUserUseCase(private val validateUserFirstNameUseCase: ValidateUserFirstNameUseCase,
                      private val validateUserLastNameUseCase: ValidateUserLastNameUseCase)
    : ObservableTransformer<User, UseCaseResult> {

    private data class ValidationAccumulator(
            val firstNameValid: Boolean = false,
            val lastNameValid: Boolean = false,
            val firstNameComplete: Boolean = false,
            val lastNameComplete: Boolean = false)

    sealed class Result : UseCaseResult {
        class InProgress : Result()
        class Success : Result()
        class Failure(val message: String) : Result()
    }

    override fun apply(upstream: Observable<User>): ObservableSource<UseCaseResult> {
        return upstream.flatMap { (firstName, lastName) ->
            Observable.merge(
                    Observable.just(firstName).compose(validateUserFirstNameUseCase),
                    Observable.just(lastName).compose(validateUserLastNameUseCase))
                    .scan(ValidationAccumulator(), { acc, result ->
                        when (result) {
                            is ValidateUserFirstNameUseCase.Result.InProgress -> acc
                            is ValidateUserFirstNameUseCase.Result.Success ->
                                acc.copy(firstNameValid = true, firstNameComplete = true)
                            is ValidateUserFirstNameUseCase.Result.Failure ->
                                acc.copy(firstNameValid = false, firstNameComplete = true)
                            is ValidateUserLastNameUseCase.Result.InProgress -> acc
                            is ValidateUserLastNameUseCase.Result.Success ->
                                acc.copy(lastNameValid = true, lastNameComplete = true)
                            is ValidateUserLastNameUseCase.Result.Failure ->
                                acc.copy(lastNameValid = false, lastNameComplete = true)
                            else -> throw IllegalArgumentException("Unexpected Result Type")
                        }
                    })
                    .skipWhile { !it.firstNameComplete || !it.lastNameComplete }
                    .map {
                        if (it.firstNameValid && it.lastNameValid) {
                            Result.Success()
                        } else {
                            Result.Failure("First or Last Name is Invalid")
                        }
                    }
                    .startWith(Result.InProgress())
        }
    }
}