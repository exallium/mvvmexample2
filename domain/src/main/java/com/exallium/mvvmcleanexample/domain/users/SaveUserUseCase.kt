package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseAction
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class SaveUserUseCase(private val validateUserFirstNameUseCase: ValidateUserFirstNameUseCase,
                      private val validateUserLastNameUseCase: ValidateUserLastNameUseCase)
    : ObservableTransformer<UseCaseAction, UseCaseResult> {

    data class Action(val user: User) : UseCaseAction
    private data class ValidationAccumulator(
            val firstNameValid: Boolean = false,
            val lastNameValid: Boolean = false,
            val firstNameComplete: Boolean = false,
            val lastNameComplete: Boolean = false)

    override fun apply(upstream: Observable<UseCaseAction>): ObservableSource<UseCaseResult> {
        return upstream.ofType(SaveUserUseCase.Action::class.java).flatMap { action ->
            val (firstName, lastName) = action.user
            val fNameAction = ValidateUserFirstNameUseCase.Action(firstName)
            val lNameAction = ValidateUserLastNameUseCase.Action(lastName)
            Observable.merge(
                    Observable.just(fNameAction).compose(validateUserFirstNameUseCase),
                    Observable.just(lNameAction).compose(validateUserLastNameUseCase))
                    .scan(ValidationAccumulator(), { acc, result ->
                        when (result) {
                            is SimpleResult.InProgress -> acc
                            is SimpleResult.Success -> onValidationSuccess(acc, result)
                            is SimpleResult.Failure -> onValidationFailure(acc, result)
                            else -> throw IllegalArgumentException("Unexpected Result Type")
                        }
                    })
                    .skipWhile { !it.firstNameComplete || !it.lastNameComplete }
                    .map {
                        if (it.firstNameValid && it.lastNameValid) {
                            SimpleResult.Success(action)
                        } else {
                            SimpleResult.Failure(action, Exception("First or Last Name is Invalid"))
                        }
                    }
                    .startWith(SimpleResult.InProgress(action))
        }
    }

    private fun onValidationSuccess(acc: ValidationAccumulator, success: SimpleResult.Success): ValidationAccumulator {
        return when (success.action) {
            is ValidateUserFirstNameUseCase.Action -> acc.copy(firstNameValid = true, firstNameComplete = true)
            is ValidateUserLastNameUseCase.Action -> acc.copy(lastNameValid = true, lastNameComplete = true)
            else -> throw IllegalArgumentException("Unexpected Action")
        }
    }

    private fun onValidationFailure(acc: ValidationAccumulator, failure: SimpleResult.Failure): ValidationAccumulator {
        return when (failure.action) {
            is ValidateUserFirstNameUseCase.Action -> acc.copy(firstNameValid = false, firstNameComplete = true)
            is ValidateUserLastNameUseCase.Action -> acc.copy(lastNameValid = false, lastNameComplete = true)
            else -> throw IllegalArgumentException("Unexpected Action")
        }
    }
}