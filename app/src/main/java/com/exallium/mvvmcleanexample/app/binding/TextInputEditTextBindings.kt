package com.exallium.mvvmcleanexample.app.binding

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputEditText
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.functions.Consumer

object TextInputEditTextBindings {

    interface StringConsumer : Consumer<Observable<String>>

    @JvmStatic
    @BindingAdapter("bindTextChanges")
    fun setChangesObservable(textInputEditText: TextInputEditText, consumer: StringConsumer) {
        consumer.accept(textInputEditText.textChanges().map(CharSequence::toString))
    }
}