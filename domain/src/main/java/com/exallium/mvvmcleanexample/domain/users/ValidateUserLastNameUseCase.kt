package com.exallium.mvvmcleanexample.domain.users

import com.exallium.mvvmcleanexample.domain.UseCaseResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ValidateUserLastNameUseCase : ObservableTransformer<String, ValidateUserLastNameUseCase.Result> {

    sealed class Result : UseCaseResult {
        class InProgress : Result()
        class Success : Result()
        class Failure(val message: String) : Result()
    }

    override fun apply(upstream: Observable<String>): ObservableSource<Result> {
        return upstream.map { it ->
            if (it.isEmpty()) {
                ValidateUserLastNameUseCase.Result.Failure("Empty Value")
            } else {
                ValidateUserLastNameUseCase.Result.Success()
            }
        }.startWith(ValidateUserLastNameUseCase.Result.InProgress())
    }
}