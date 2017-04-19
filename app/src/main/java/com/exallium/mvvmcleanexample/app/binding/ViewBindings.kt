package com.exallium.mvvmcleanexample.app.binding

import android.databinding.BindingAdapter
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.functions.Consumer

object ViewBindings {

    interface ClickConsumer : Consumer<Observable<Unit>>

    @JvmStatic
    @BindingAdapter("bindClicks")
    fun setClicksObservable(view: View, consumer: ClickConsumer) {
        consumer.accept(view.clicks())
    }
}