package com.exallium.mvvmcleanexample.app.binding

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout

object TextInputLayoutBindings {
    @JvmStatic
    @BindingAdapter("bindError")
    fun bindTextInputLayoutError(textInputLayout: TextInputLayout, error: String) {
        textInputLayout.error = error
        textInputLayout.isErrorEnabled = error.isNotEmpty()
    }
}