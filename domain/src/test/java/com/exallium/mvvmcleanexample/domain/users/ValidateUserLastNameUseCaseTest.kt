package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test

class ValidateUserLastNameUseCaseTest {

    val testSubject = ValidateUserLastNameUseCase()

    val testObserver = TestObserver<UseCaseResult>()

    @Test
    fun apply_whenTextIsValid_inProgressThenSuccess() {
        // WHEN
        Observable.just(ValidateUserLastNameUseCase.Action("lastName"))
                .compose(testSubject)
                .subscribe(testObserver)

        // THEN
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SimpleResult.InProgress }
        testObserver.assertValueAt(1) { it is SimpleResult.Success }
    }

    @Test
    fun apply_whenTextIsNotValid_inProgressThenFailure() {
        // WHEN
        Observable.just(ValidateUserLastNameUseCase.Action(""))
                .compose(testSubject)
                .subscribe(testObserver)

        // THEN
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it is SimpleResult.InProgress }
        testObserver.assertValueAt(1) { it is SimpleResult.Failure }
    }
}