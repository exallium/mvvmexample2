package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserEditViewModelTest {

    companion object {
        val ERR = "asdf"
        val EXCEPTION = Exception(ERR)
    }

    lateinit var testSubject: UserEditViewModel

    @Mock
    lateinit var model: UserEditModel

    @Mock
    lateinit var router: UiRouter

    val saveClicks = PublishSubject.create<Unit>()
    val firstNameChanges = PublishSubject.create<CharSequence>()
    val lastNameChanges = PublishSubject.create<CharSequence>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(model.results(any(), any(), any())).thenReturn(Observable.empty())
    }

    @Test
    fun initializeStreams_delegatesToModel() {
        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(model).results(any(), any(), any())
    }

    @Test
    fun lastNameChange_inProgress_doesNothing() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.InProgress())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(testSubject.lastNameError.get().isEmpty())
    }

    @Test
    fun firstNameChange_inProgress_doesNothing() {
        // GIVEN
        given_routerEmitsResult(ValidateUserFirstNameUseCase.Result.InProgress())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(testSubject.firstNameError.get().isEmpty())
    }

    @Test
    fun userSave_inProgress_doesNothing() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.InProgress())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verifyZeroInteractions(router)
    }

    @Test
    fun lastNameChange_success_clearsError() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.Success())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert("" == testSubject.lastNameError.get())
    }

    @Test
    fun firstNameChange_success_clearsError() {
        // GIVEN
        given_routerEmitsResult(ValidateUserFirstNameUseCase.Result.Success())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert("" == testSubject.firstNameError.get())
    }

    @Test
    fun userSave_success_routerGoesBack() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.Success())

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).displayMessage(any())
        verify(router).goBack()
    }

    @Test
    fun lastNameChange_failed_setsError() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.Failure(ERR))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.lastNameError.get())
    }

    @Test
    fun firstNameChange_failed_setsError() {
        // GIVEN
        given_routerEmitsResult(ValidateUserFirstNameUseCase.Result.Failure(ERR))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.firstNameError.get())
    }

    @Test
    fun userSave_failed_routerDisplaysError() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.Failure(ERR))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).displayError(ERR)
    }

    @Test
    fun streamError_routerDisplayAndGoBack() {
        // GIVEN
        whenever(model.results(any(), any(), any())).thenReturn(Observable.error(EXCEPTION))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).displayError(ERR)
        verify(router).goBack()
    }

    private fun when_testSubjectInitialized() {
        testSubject = UserEditViewModel(model, router)
    }

    private fun given_routerEmitsResult(useCaseResult: UseCaseResult) {
        whenever(model.results(any(), any(), any())).thenReturn(Observable.just(useCaseResult))
    }
}