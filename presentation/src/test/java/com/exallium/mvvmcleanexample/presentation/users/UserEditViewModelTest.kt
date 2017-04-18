package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserEditViewModelTest {

    companion object {
        val ERR = "asdf"
        val EXCEPTION = Exception(ERR)
    }

    @InjectMocks
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
        testSubject.lastNameError.set(ERR)
        given_routerEmitsResult(SimpleResult.InProgress(ValidateUserLastNameUseCase.Action("")))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.lastNameError.get())
    }

    @Test
    fun firstNameChange_inProgress_doesNothing() {
        // GIVEN
        testSubject.firstNameError.set(ERR)
        given_routerEmitsResult(SimpleResult.InProgress(ValidateUserLastNameUseCase.Action("")))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.firstNameError.get())
    }

    @Test
    fun userSave_inProgress_doesNothing() {
        // GIVEN
        given_routerEmitsResult(SimpleResult.InProgress(SaveUserUseCase.Action(mock())))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verifyZeroInteractions(router)
    }

    @Test
    fun lastNameChange_success_clearsError() {
        // GIVEN
        testSubject.lastNameError.set(ERR)
        given_routerEmitsResult(SimpleResult.Success(ValidateUserLastNameUseCase.Action("")))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert("" == testSubject.lastNameError.get())
    }

    @Test
    fun firstNameChange_success_clearsError() {
        // GIVEN
        testSubject.firstNameError.set(ERR)
        given_routerEmitsResult(SimpleResult.Success(ValidateUserFirstNameUseCase.Action("")))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert("" == testSubject.firstNameError.get())
    }

    @Test
    fun userSave_success_routerGoesBack() {
        // GIVEN
        given_routerEmitsResult(SimpleResult.Success(SaveUserUseCase.Action(mock())))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).goBack()
    }

    @Test
    fun lastNameChange_failed_setsError() {
        // GIVEN
        testSubject.lastNameError.set("")
        given_routerEmitsResult(SimpleResult.Failure(ValidateUserLastNameUseCase.Action(""), EXCEPTION))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.lastNameError.get())
    }

    @Test
    fun firstNameChange_failed_setsError() {
        // GIVEN
        testSubject.firstNameError.set("")
        given_routerEmitsResult(SimpleResult.Failure(ValidateUserFirstNameUseCase.Action(""), EXCEPTION))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        assert(ERR == testSubject.firstNameError.get())
    }

    @Test
    fun userSave_failed_routerDisplaysError() {
        // GIVEN
        given_routerEmitsResult(SimpleResult.Failure(SaveUserUseCase.Action(mock()), EXCEPTION))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).displayMessage(ERR)
    }

    @Test
    fun streamError_routerDisplayAndGoBack() {
        // GIVEN
        whenever(model.results(any(), any(), any())).thenReturn(Observable.error(EXCEPTION))

        // WHEN
        when_testSubjectInitialized()

        // THEN
        verify(router).displayMessage(ERR)
        verify(router).goBack()
    }

    private fun when_testSubjectInitialized() {
        testSubject.initializeStream(saveClicks, firstNameChanges, lastNameChanges)
    }

    private fun given_routerEmitsResult(useCaseResult: UseCaseResult) {
        whenever(model.results(any(), any(), any())).thenReturn(Observable.just(useCaseResult))
    }
}