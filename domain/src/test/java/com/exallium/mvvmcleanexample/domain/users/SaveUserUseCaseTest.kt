package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SaveUserUseCaseTest {

    companion object {
        val ERR = "asdf"
    }

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
                Observable.just(ValidateUserFirstNameUseCase.Result.Failure(ERR)))
    }

    private fun given_invalidLastName() {
        whenever(validateUserLastNameUseCase.apply(any())).thenReturn(
                Observable.just(ValidateUserLastNameUseCase.Result.Failure(ERR)))
    }

    private fun given_validFirstName() {
        whenever(validateUserFirstNameUseCase.apply(any())).thenReturn(
                Observable.just(ValidateUserFirstNameUseCase.Result.Success()))
    }

    private fun given_validLastName() {
        whenever(validateUserLastNameUseCase.apply(any())).thenReturn(
                Observable.just(ValidateUserLastNameUseCase.Result.Success()))
    }

    private fun when_actionSubmit() {
        Observable.just(user)
                .compose(testSubject)
                .subscribe(testObserver)
    }

    private fun then_firstIsInProgressSecondIsFailure() {
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SaveUserUseCase.Result.InProgress }
        testObserver.assertValueAt(1) { it is SaveUserUseCase.Result.Failure }
    }

    private fun then_firstIsInProgressSecondIsSuccess() {
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SaveUserUseCase.Result.InProgress }
        testObserver.assertValueAt(1) { it is SaveUserUseCase.Result.Success }
    }
}