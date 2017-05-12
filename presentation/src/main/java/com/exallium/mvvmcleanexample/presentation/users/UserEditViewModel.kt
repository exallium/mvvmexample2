package com.exallium.mvvmcleanexample.presentation.users

import android.databinding.Bindable
import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import com.exallium.mvvmcleanexample.presentation.utils.*
import com.exallium.mvvmcleanexample.presentation.utils.delegates.DisposableProperty
import com.exallium.mvvmcleanexample.presentation.utils.notifiable.BaseNotifiableObservable
import com.exallium.mvvmcleanexample.presentation.utils.notifiable.NotifiableObservable
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.Disposable
import android.databinding.BindingAdapter

class UserEditViewModel(
        private val userEditModel: UserEditModel,
        private val router: UiRouter,
        private val firstNameRelay: BehaviorRelay<String> = BehaviorRelay.create(),
        private val lastNameRelay: BehaviorRelay<String> = BehaviorRelay.create(),
        private val savedUserRelay: PublishRelay<User> = PublishRelay.create(),
        disposableProperty: DisposableProperty = disposable(null),
        notifiableObservable: NotifiableObservable = BaseNotifiableObservable())
    : NotifiableObservable by notifiableObservable {

    init {
        setDelegator(this)
    }

    /* symbol meaning:
    >> : bind direction left to right, takes initial value, future changes are propagated
    << : bind direction right to left, takes initial value, future changes are propagated
    >| : bind direction left to right, takes initial value, future changes are not propagated
    |< : bind direction right to left, takes initial value, future changes are not propagated
     */

    /** firstName & lastName explanation
     *
     * View model to view ( |< )
     *
     * When the xml binding expression is defined as:
     *
     * ```
     * bind:text="@{viewModel.firstName}"
     * bind:text="@{viewModel.lastName}"
     *```
     *
     * The [UserEditViewBinding] class will take the values of these properties
     * and pass them to the view through a [BindingAdapter] (see [TextViewBindingAdapters]).
     *
     * As these properties are not annotated with [Bindable], the generated binding class
     * [UserEditViewBinding] will not listen for future changes on them.
     *
     * The initial values are taken at the point where the [UserEditViewModel]
     * is set on the [UserEditViewBinding].
     *
     * View to view model ( >> )
     *
     * When the xml binding expression is defined as an inverse binding:
     *
     * ```
     * bind:text="@={viewModel.firstName}"
     * bind:text="@={viewModel.lastName}"
     *```
     *
     * The view can also change the value of the property,
     * instead of simply reading the value of the property.
     *
     */

    // view |< >> view model
    var firstName: String by distinctObservable(
            initialValue = "",
            onChange = firstNameRelay::accept)

    // view |< >> view model
    var lastName: String by distinctObservable(
            initialValue = "",
            onChange = lastNameRelay::accept)

    /** firstNameError & lastNameError explanation
     *
     * View model to view ( << )
     *
     * When the xml binding expression is defined as:
     *
     * ```
     * bind:error="@{viewModel.firstNameError}"
     * bind:error="@{viewModel.lastNameError}"
     *```
     *
     * The [UserEditViewBinding] class will take the values of these properties
     * and pass them to the view through a [BindingAdapter] (see [TextViewBindingAdapters]).
     *
     * Unlike [firstName] and [lastName], as the property getters are annotated with [Bindable],
     * the generated binding class [UserEditViewBinding] will listen for future changes on them.
     *
     * These properties have private setters, so inverse binding expressions in xml are impossible.
     *
     * This can be thought of as a one-way property binding as the view is not able to change the
     * value of these properties.
     */

    // view << view model
    @get:Bindable
    var firstNameError: String by bindable("")
        private set

    // view << view model
    @get:Bindable
    var lastNameError: String by bindable("")
        private set

    private var disposable: Disposable? by disposableProperty

    // view >> view model
    fun saveUser() {
        savedUserRelay.accept {
            User(firstName, lastName)
        }
    }

    fun start() {
        disposable = userEditModel.results(
                saveClicks = savedUserRelay,
                firstNameChanges = firstNameRelay,
                lastNameChanges = lastNameRelay)
                .subscribe(this::handleResult, this::handleError)
    }

    fun stop() {
        disposable = null
    }

    private fun handleResult(useCaseResult: UseCaseResult) {
        when (useCaseResult) {
            is ValidateUserFirstNameUseCase.Result.InProgress -> handleInProgress()
            is ValidateUserFirstNameUseCase.Result.Success -> firstNameError = ""
            is ValidateUserFirstNameUseCase.Result.Failure -> firstNameError = useCaseResult.message

            is ValidateUserLastNameUseCase.Result.InProgress -> handleInProgress()
            is ValidateUserLastNameUseCase.Result.Success -> lastNameError = ""
            is ValidateUserLastNameUseCase.Result.Failure -> lastNameError = useCaseResult.message

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
        router.displayError(throwable.message ?: "")
        router.goBack()
    }

    private fun handleInProgress() {
        // TODO
    }
}
