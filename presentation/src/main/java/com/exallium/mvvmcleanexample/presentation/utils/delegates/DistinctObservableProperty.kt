package com.exallium.mvvmcleanexample.presentation.utils.delegates

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

/**
 * An implementation of [ObservableProperty] that performs an equality check before changing the internal value;
 * the change is propagated if they are not equal.
 */
open class DistinctObservableProperty<T>(initialValue: T) : ObservableProperty<T>(initialValue) {

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        return oldValue != newValue
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        super.afterChange(property, oldValue, newValue)
    }
}