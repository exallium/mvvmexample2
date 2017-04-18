package com.exallium.mvvmcleanexample.presentation.users

import android.databinding.ObservableField
import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class UserEditViewModel(private val userEditModel: UserEditModel,
                        private val router: UiRouter) {

    val firstName = ObservableField<String>("")
    val firstNameError = ObservableField<String>("")
    val lastName = ObservableField<String>("")
    val lastNameError = ObservableField<String>("")

    private val compositeDisposable = CompositeDisposable()

    fun initializeStream(saveClicks: Observable<Unit>,
                         firstNameChanges: Observable<CharSequence>,
                         lastNameChanges: Observable<CharSequence>) {
        userEditModel.results(saveClicks.map { User(firstName.get(), lastName.get()) },
                firstNameChanges.map(CharSequence::toString),
                lastNameChanges.map(CharSequence::toString))
                .subscribe(this::handleResult, this::handleError)
    }

    fun cancel() {
        compositeDisposable.clear()
    }

    private fun handleResult(useCaseResult: UseCaseResult) {
        when (useCaseResult) {
            is SimpleResult.InProgress -> handleInProgress(useCaseResult)
            is SimpleResult.Success -> handleSuccess(useCaseResult)
            is SimpleResult.Failure -> handleFailure(useCaseResult)
        }
    }

    private fun handleError(throwable: Throwable) {
        router.displayMessage(throwable.message?:"")
        router.goBack()
    }

    private fun handleInProgress(result: SimpleResult.InProgress) {
        // TODO
    }

    private fun handleSuccess(result: SimpleResult.Success) {
        when (result.action) {
            is ValidateUserFirstNameUseCase.Action -> firstNameError.set("")
            is ValidateUserLastNameUseCase.Action -> lastNameError.set("")
            is SaveUserUseCase.Action -> router.goBack()
        }
    }

    private fun handleFailure(result: SimpleResult.Failure) {
        when (result.action) {
            is ValidateUserFirstNameUseCase.Action -> firstNameError.set(result.throwable.message?:"")
            is ValidateUserLastNameUseCase.Action -> lastNameError.set(result.throwable.message?:"")
            is SaveUserUseCase.Action -> router.displayMessage(result.throwable.message?:"Error")
        }
    }

}