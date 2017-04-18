package com.exallium.mvvmcleanexample.domain.actions

sealed class SimpleResult(val action: UseCaseAction): UseCaseResult {
    class InProgress(a: UseCaseAction) : SimpleResult(a)
    class Success(a: UseCaseAction) : SimpleResult(a)
    class Failure(a: UseCaseAction, val throwable: Throwable) : SimpleResult(a)
}