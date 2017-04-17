package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.actions.SimpleResult
import com.exallium.mvvmcleanexample.domain.actions.UseCaseAction
import com.exallium.mvvmcleanexample.domain.actions.UseCaseResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ValidateUserLastNameUseCase : ObservableTransformer<UseCaseAction, UseCaseResult> {

    data class Action(val lastName: String) : UseCaseAction

    override fun apply(upstream: Observable<UseCaseAction>): ObservableSource<UseCaseResult> {
        return upstream.ofType(Action::class.java).flatMap { it ->
            Observable.just(if (it.lastName.isEmpty()) {
                SimpleResult.Failure(it, Exception("Empty Value"))
            } else {
                SimpleResult.Success(it)
            }).startWith(SimpleResult.InProgress(it))
        }
    }
}