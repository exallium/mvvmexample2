package com.exallium.mvvmcleanexample.presentation.users

import android.databinding.ObservableField
import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class UserEditViewModel(private val userEditModel: UserEditModel,
                        private val router: UiRouter) {

    val firstName = ObservableField<String>("")
    val firstNameError = ObservableField<String>("")
    val lastName = ObservableField<String>("")
    val lastNameError = ObservableField<String>("")

    private val compositeDisposable = CompositeDisposable()

    private val clickSubject = PublishSubject.create<User>()
    private val firstNameSubject = PublishSubject.create<String>()
    private val lastNameSubject = PublishSubject.create<String>()

    init {
        userEditModel.results(
                clickSubject,
                firstNameSubject.distinctUntilChanged(),
                lastNameSubject.distinctUntilChanged()
        ).subscribe(this::handleResult, this::handleError)
    }

    fun save() {
        clickSubject.onNext(User(firstName.get(), lastName.get()))
    }

    fun firstNameChanged(text: CharSequence) {
        firstName.set(text.toString())
        firstNameSubject.onNext(text.toString())
    }

    fun lastNameChanged(text: CharSequence) {
        lastName.set(text.toString())
        lastNameSubject.onNext(text.toString())
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