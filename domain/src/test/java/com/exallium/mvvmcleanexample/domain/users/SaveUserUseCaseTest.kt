package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SaveUserUseCaseTest {

    @InjectMocks
    lateinit var testSubject : SaveUserUseCase

    @Mock
    lateinit var validateUserFirstNameUseCase: ValidateUserFirstNameUseCase

    @Mock
    lateinit var validateUserLastNameUseCase: ValidateUserLastNameUseCase

    val user = User("", "")
    val testObserver = TestObserver<UseCaseResult>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun apply_whenFirstNameIsInvalid_getInProgressThenFailure() {
        // GIVEN
        given_invalidFirstName()
        given_validLastName()

        // WHEN
        when_actionSubmit()

        // THEN
        then_firstIsInProgressSecondIsFailure()
    }

    @Test
    fun apply_whenLastNameIsInvalid_getInProgressThenFailure() {
        // GIVEN
        given_validFirstName()
        given_invalidLastName()

        // WHEN
        when_actionSubmit()

        // THEN
        then_firstIsInProgressSecondIsFailure()
    }

    @Test
    fun apply_whenBothNamesAreInvalid_getInProgressThenFailure() {
        // GIVEN
        given_invalidFirstName()
        given_invalidLastName()

        // WHEN
        when_actionSubmit()

        // THEN
        then_firstIsInProgressSecondIsFailure()
    }

    @Test
    fun apply_whenBothNamesAreValid_getInProgressThenSuccess() {
        // GIVEN
        given_validFirstName()
        given_validLastName()

        // WHEN
        when_actionSubmit()

        // THEN
        then_firstIsInProgressSecondIsSuccess()
    }

    private fun given_invalidFirstName() {
        whenever(validateUserFirstNameUseCase.apply(any())).thenReturn(
                Observable.just(SimpleResult.Failure(mock<ValidateUserFirstNameUseCase.Action>(), mock())))
    }

    private fun given_invalidLastName() {
        whenever(validateUserLastNameUseCase.apply(any())).thenReturn(
                Observable.just(SimpleResult.Failure(mock<ValidateUserLastNameUseCase.Action>(), mock())))
    }

    private fun given_validFirstName() {
        whenever(validateUserFirstNameUseCase.apply(any())).thenReturn(
                Observable.just(SimpleResult.Success(mock<ValidateUserFirstNameUseCase.Action>())))
    }

    private fun given_validLastName() {
        whenever(validateUserLastNameUseCase.apply(any())).thenReturn(
                Observable.just(SimpleResult.Success(mock<ValidateUserLastNameUseCase.Action>())))
    }

    private fun when_actionSubmit() {
        Observable.just(SaveUserUseCase.Action(user))
                .compose(testSubject)
                .subscribe(testObserver)
    }

    private fun then_firstIsInProgressSecondIsFailure() {
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SimpleResult.InProgress }
        testObserver.assertValueAt(1) { it is SimpleResult.Failure }
    }

    private fun then_firstIsInProgressSecondIsSuccess() {
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SimpleResult.InProgress }
        testObserver.assertValueAt(1) { it is SimpleResult.Success }
    }
}