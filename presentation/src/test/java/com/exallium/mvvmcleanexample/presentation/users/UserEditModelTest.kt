package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserEditModelTest {

    @InjectMocks
    lateinit var testSubject : UserEditModel

    @Mock
    lateinit var saveUserUseCase: SaveUserUseCase

    @Mock
    lateinit var validateUserFirstNameUseCase: ValidateUserFirstNameUseCase

    @Mock
    lateinit var validateUserLastNameUseCase: ValidateUserLastNameUseCase

    val saveClicks = PublishSubject.create<User>()
    val firstNameChanges = PublishSubject.create<String>()
    val lastNameChanges = PublishSubject.create<String>()
    val testObserver = TestObserver<UseCaseResult>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(saveUserUseCase.apply(any())).thenReturn(Observable.just(SaveUserUseCase.Result.InProgress()))
        whenever(validateUserFirstNameUseCase.apply(any())).thenReturn(Observable.just(ValidateUserFirstNameUseCase.Result.InProgress()))
        whenever(validateUserLastNameUseCase.apply(any())).thenReturn(Observable.just(ValidateUserLastNameUseCase.Result.InProgress()))
    }

    @Test
    fun action_afterResultsWired_eachUseCaseIsApplied() {
        // GIVEN
        testSubject.results(saveClicks, firstNameChanges, lastNameChanges).subscribe(testObserver)

        // WHEN
        saveClicks.onNext(User("", ""))

        // THEN
        verify(saveUserUseCase).apply(any())
        verify(validateUserFirstNameUseCase).apply(any())
        verify(validateUserLastNameUseCase).apply(any())
    }
}