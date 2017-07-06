package com.exallium.mvvmcleanexample.app.binding

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout

private const val BIND_ERROR = "bind:error"

/**
 * Binds a [String] to [TextInputLayout.setError]
 */
@BindingAdapter(BIND_ERROR)
fun TextInputLayout.bindError(error: String?) {
    this.error = error
    isErrorEnabled = error?.isNotEmpty() ?: false
}