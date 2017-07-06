package com.exallium.mvvmcleanexample.app.binding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.databinding.adapters.TextViewBindingAdapter
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.android.databinding.library.baseAdapters.R
private const val BIND_TEXT = "bind:text"
private const val BIND_TEXT_ATTR_CHANGED = "bind:textAttrChanged"
private const val BIND_ON_TEXT_CHANGED = "bind:onTextChanged"

// cannot use typealiases here
interface OnTextChanged {
    fun invoke(text: CharSequence)
}

/**
 * Makes the text property of [TextView] bindable,
 * where [TextView.getText] is an [InverseBindingAdapter],
 * and [TextView.setText] is a [BindingAdapter].
 */
var TextView.boundText: String?
    @InverseBindingAdapter(attribute = BIND_TEXT, event = BIND_TEXT_ATTR_CHANGED)
    get() = text.toString()
    @BindingAdapter(value = BIND_TEXT)
    set(value) = if (value != text.toString()) text = value else Unit

/**
 * A simplified alternative to the framework variant [TextViewBindingAdapter.setTextWatcher].
 *
 * Unlike the framework variant, this only supports binding to the [TextWatcher.onTextChanged] callback,
 * and receives only the [CharSequence], allowing simpler databinding expressions in xml.
 *
 * note: This is expressed as an extension function because when translated to Java,
 * the receiver object becomes the first parameter of the method, which results in a valid [BindingAdapter].
 */
@BindingAdapter(value = *arrayOf(
        BIND_ON_TEXT_CHANGED,
        BIND_TEXT_ATTR_CHANGED), requireAll = false)
fun TextView.setTextWatcher(
        onTextChanged: OnTextChanged?,
        textAttrChanged: InverseBindingListener?) {

    trackListener(
            newListener = tryCreateTextWatcher(onTextChanged, textAttrChanged),
            listenerResourceId = R.id.textWatcher,
            attachListener = {
                addTextChangedListener(it)
            },
            detachListener = {
                removeTextChangedListener(it)
            })
}

private fun tryCreateTextWatcher(onTextChanged: OnTextChanged?, textAttrChanged: InverseBindingListener?): TextWatcher? {
    return if (onTextChanged != null || textAttrChanged != null) {
        createTextWatcher(onTextChanged, textAttrChanged)
    } else null
}

private fun createTextWatcher(onTextChanged: OnTextChanged?, textAttrChanged: InverseBindingListener?): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore until needed
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s)
            textAttrChanged?.onChange()
        }

        override fun afterTextChanged(s: Editable) {
            // ignore until needed
        }
    }
}
