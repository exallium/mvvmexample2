package com.exallium.mvvmcleanexample.presentation.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import com.exallium.mvvmcleanexample.domain.users.SaveUserUseCase
import com.exallium.mvvmcleanexample.domain.users.User
import com.exallium.mvvmcleanexample.domain.users.ValidateUserFirstNameUseCase
import com.exallium.mvvmcleanexample.domain.users.ValidateUserLastNameUseCase
import com.exallium.mvvmcleanexample.presentation.BR
import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import com.exallium.mvvmcleanexample.presentation.utils.PropertyCacher
import com.exallium.mvvmcleanexample.presentation.utils.delegates.DisposableProperty
import com.exallium.mvvmcleanexample.presentation.utils.notifiable.NotifiableObservable
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserEditViewModelTest {

    companion object {
        const val ERR = "asdf"
        val EXCEPTION = Exception(ERR)
    }

    init {
        PropertyCacher.bindableResourceClass = BR::class.java
    }

    lateinit var testSubject: UserEditViewModel

    @Mock lateinit var model: UserEditModel
    @Mock lateinit var router: UiRouter
    @Mock lateinit var firstNameRelay: BehaviorRelay<String>
    @Mock lateinit var lastNameRelay: BehaviorRelay<String>
    @Mock lateinit var savedUserRelay: PublishRelay<User>
    @Mock lateinit var disposableProperty: DisposableProperty
    @Mock lateinit var notifiableObservable: NotifiableObservable

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(model.results(any(), any(), any())).thenReturn(Observable.empty())
    }

    @Test
    fun `Delegates to notifiableObservable in constructor`() {
        // GIVEN, WHEN
        initTestSubject()

        // THEN
        verify(notifiableObservable).setDelegator(testSubject)
        verifyNoMoreInteractions(notifiableObservable)
    }

    @Test
    fun `Subscribes to model on start`() {
        // GIVEN
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        inOrder(model, disposableProperty).apply {
            verify(model).results(
                    saveClicks = savedUserRelay,
                    firstNameChanges = firstNameRelay,
                    lastNameChanges = lastNameRelay)
            verify(disposableProperty).setValue(any(), any(), any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `Unsubscribes from model on stop`() {
        // GIVEN
        initTestSubject()

        // WHEN
        testSubject.stop()

        // THEN
        verify(disposableProperty).setValue(any(), any(), eq(null))
        verifyNoMoreInteractions(disposableProperty)
    }

    @Test
    fun `Notifies firstNameRelay on init with provided defaults`() {
        // GIVEN
        val expectedFirstName = ""

        // WHEN
        initTestSubject()

        // THEN
        verify(firstNameRelay).accept(eq(expectedFirstName))
        verifyNoMoreInteractions(firstNameRelay)
    }

    @Test
    fun `Notifies lastNameRelay on init with provided defaults`() {
        // GIVEN
        val expectedLastName = ""

        // WHEN
        initTestSubject()

        // THEN
        verify(lastNameRelay).accept(eq(expectedLastName))
        verifyNoMoreInteractions(lastNameRelay)
    }

    @Test
    fun `Notifies firstNameRelay when firstName changed`() {
        // GIVEN
        val expectedFirstName = "Joe"
        initTestSubject()
        clearInvocations(firstNameRelay, lastNameRelay)

        // WHEN
        testSubject.firstName = expectedFirstName

        // THEN
        verify(firstNameRelay).accept(eq(expectedFirstName))
        verifyNoMoreInteractions(firstNameRelay)
        verifyZeroInteractions(lastNameRelay, savedUserRelay)
    }

    @Test
    fun `Notifies lastNameRelay when lastName changed`() {
        // GIVEN
        val expectedLastName = "Joe"
        initTestSubject()
        clearInvocations(firstNameRelay, lastNameRelay)

        // WHEN
        testSubject.lastName = expectedLastName

        // THEN
        verify(lastNameRelay).accept(eq(expectedLastName))
        verifyNoMoreInteractions(lastNameRelay)
        verifyZeroInteractions(firstNameRelay, savedUserRelay)
    }

    @Test
    fun `Notifies savedUserRelay when save clicked`() {
        // GIVEN
        initTestSubject()
        val expectedFirstName = "Joe"
        val expectedLastName = "Bloggs"
        testSubject.firstName = expectedFirstName
        testSubject.lastName = expectedLastName
        clearInvocations(firstNameRelay, lastNameRelay)
        val expectedUser = User(
                firstName = expectedFirstName,
                lastName = expectedLastName)


        // WHEN
        testSubject.saveUser()

        // THEN
        verify(savedUserRelay).accept(eq(expectedUser))
        verifyNoMoreInteractions(firstNameRelay)
        verifyZeroInteractions(savedUserRelay)
    }

    @Test
    fun `FirstNameError not changed when firstName invalid and validation in progress`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.InProgress())
        initTestSubject()
        testSubject.start()
        clearInvocations(notifiableObservable)

        // WHEN
        testSubject.firstName = ""

        // THEN
        verifyZeroInteractions(notifiableObservable)
    }

    @Test
    fun `LastNameError not changed when lastName invalid and inProgress`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.InProgress())
        initTestSubject()
        testSubject.start()
        clearInvocations(notifiableObservable)

        // WHEN
        testSubject.lastName = ""

        // THEN
        verifyZeroInteractions(notifiableObservable)
    }

    @Test
    fun `No interactions with router when user save in progress`() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.InProgress())
        initTestSubject()
        testSubject.start()

        // WHEN
        testSubject.saveUser()

        // THEN
        verifyZeroInteractions(router)
    }

    @Test
    fun `Error cleared when lastName changed successfully`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.Success())
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        assert("" == testSubject.lastNameError)
    }

    @Test
    fun `Error cleared when firstName changed successfully`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserFirstNameUseCase.Result.Success())
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        assert("" == testSubject.firstNameError)
    }

    @Test
    fun `Error set when lastName validation is unsuccessful`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserLastNameUseCase.Result.Failure(ERR))
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        assert(ERR == testSubject.lastNameError)
    }

    @Test
    fun `Error set when firstName validation is unsuccessful`() {
        // GIVEN
        given_routerEmitsResult(ValidateUserFirstNameUseCase.Result.Failure(ERR))
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        assert(ERR == testSubject.firstNameError)
    }

    @Test
    fun `Router goes back when user saved successfully`() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.Success())
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        verify(router).displayMessage(any())
        verify(router).goBack()
    }

    @Test
    fun `Router displays error when user save is unsuccessful`() {
        // GIVEN
        given_routerEmitsResult(SaveUserUseCase.Result.Failure(ERR))
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        verify(router).displayError(ERR)
    }

    @Test
    fun `Router displays error and goes back on stream error`() {
        // GIVEN
        whenever(model.results(any(), any(), any())).thenReturn(Observable.error(EXCEPTION))
        initTestSubject()

        // WHEN
        testSubject.start()

        // THEN
        verify(router).displayError(ERR)
        verify(router).goBack()
    }

    private fun initTestSubject() {
        testSubject = UserEditViewModel(
                userEditModel = model,
                router = router,
                firstNameRelay = firstNameRelay,
                lastNameRelay = lastNameRelay,
                savedUserRelay = savedUserRelay,
                disposableProperty = disposableProperty,
                notifiableObservable = notifiableObservable)
    }

    private fun given_routerEmitsResult(useCaseResult: UseCaseResult) {
        whenever(model.results(any(), any(), any())).thenReturn(Observable.just(useCaseResult))
    }
}