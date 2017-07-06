package com.exallium.mvvmcleanexample.app.binding

import android.databinding.InverseBindingAdapter
import android.databinding.adapters.ListenerUtil
import android.support.annotation.IdRes
import android.view.View

typealias ListenerAction<V, Listener> = V.(Listener) -> Unit

/**
 * Utility function for [InverseBindingAdapter]s.
 */
inline fun <V : View, L> V.trackListener(
        newListener: L?,
        @IdRes listenerResourceId: Int,
        attachListener: ListenerAction<V, L>,
        detachListener: ListenerAction<V, L>) {

    val oldListener = ListenerUtil.trackListener<L>(this, newListener, listenerResourceId)

    oldListener?.let {
        detachListener(it)
    }

    newListener?.let {
        attachListener(it)
    }
}