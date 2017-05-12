package com.exallium.mvvmcleanexample.presentation.utils.delegates

import io.reactivex.disposables.Disposable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property delegate for [Disposable] objects that performs cleanup on assignment.
 *
 * When a new value is assigned, [setValue] will call [Disposable.dispose] on the previous value if available.
 * @param disposable the new value
 */
class DisposableProperty(private var disposable: Disposable?) : ReadWriteProperty<Any?, Disposable?> {

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Disposable?) {
        disposable?.dispose()
        disposable = value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Disposable? = disposable
}