package com.exallium.mvvmcleanexample.presentation.users

import android.databinding.ObservableField
import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class UserEditViewModel(userEditModel: UserEditModel,
                        private val router: UiRouter) {

    val firstNameError = ObservableField<String>("")
    val lastNameError = ObservableField<String>("")

    private val compositeDisposable = CompositeDisposable()

    private val clickRelay = PublishRelay.create<User>()
    private val firstNameRelay = BehaviorRelay.create<String>()
    private val lastNameRelay = BehaviorRelay.create<String>()

    init {
        compositeDisposable.add(userEditModel.results(clickRelay, firstNameRelay, lastNameRelay)
                .subscribe(this::handleResult, this::handleError))
    }

    fun setSaveClicks(obs: Observable<Unit>) {
        compositeDisposable.add(obs
                .map { User(firstNameRelay.value, lastNameRelay.value) }
                .subscribe(clickRelay))
    }

    fun setFirstNameChanged(obs: Observable<String>) {
        compositeDisposable.add(obs.subscribe(firstNameRelay))
    }

    fun setLastNameChanged(obs: Observable<String>) {
        compositeDisposable.add(obs.subscribe(lastNameRelay))
    }

    fun cancel() {
        compositeDisposable.clear()
    }

    private fun handleResult(useCaseResult: UseCaseResult) {
        when (useCaseResult) {
            is ValidateUserFirstNameUseCase.Result.InProgress -> handleInProgress()
            is ValidateUserFirstNameUseCase.Result.Success -> firstNameError.set("")
            is ValidateUserFirstNameUseCase.Result.Failure -> firstNameError.set(useCaseResult.message)

            is ValidateUserLastNameUseCase.Result.InProgress -> handleInProgress()
            is ValidateUserLastNameUseCase.Result.Success -> lastNameError.set("")
            is ValidateUserLastNameUseCase.Result.Failure -> lastNameError.set(useCaseResult.message)

            is SaveUserUseCase.Result.InProgress -> handleInProgress()
            is SaveUserUseCase.Result.Success -> {
                router.displayMessage("Success")
                router.goBack()
            }
            is SaveUserUseCase.Result.Failure -> {
                router.displayError(useCaseResult.message)
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        router.displayError(throwable.message?:"")
        router.goBack()
    }

    private fun handleInProgress() {
        // TODO
    }
}