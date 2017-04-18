package com.exallium.mvvmcleanexample.app.nav

import com.exallium.mvvmcleanexample.presentation.nav.UiRouter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AppRouter : UiRouter {
    sealed class Transition {
        class GoBack : Transition()
        class DisplayMessage(val message: String) : Transition()
        class DisplayError(val message: String): Transition()
    }

    private val routeSubject = PublishSubject.create<Transition>()

    override fun goBack() {
        routeSubject.onNext(Transition.GoBack())
    }

    override fun displayMessage(message: String) {
        routeSubject.onNext(Transition.DisplayMessage(message))
    }

    override fun displayError(message: String) {
        routeSubject.onNext(Transition.DisplayError(message))
    }

    fun transitions(): Observable<Transition> = routeSubject
}