package com.exallium.mvvmcleanexample.presentation.utils.delegates

import android.databinding.Observable
import com.exallium.mvvmcleanexample.presentation.utils.PropertyCacher
import kotlin.reflect.KProperty


/**
 * A property delegate for properties of [Observable] objects.
 */
abstract class BindableProperty<T, in O : Observable>(
        private val initialValue: T,
        private val bindableResourceId: Int = USE_PROPERTY_CACHER) : DistinctObservableProperty<T>(initialValue) {

    companion object{
        const val USE_PROPERTY_CACHER = -1
    }

    private var propertyId: Int = 0
    private lateinit var observable: O

    operator fun provideDelegate(observable: O, property: KProperty<*>): BindableProperty<T, O> {
        this.observable = observable
        this.propertyId = PropertyCacher.bindableResourceIdForKProperty(property)
        return this
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        notify(observable, propertyId, newValue)
    }

    abstract fun notify(observable: O, propertyId: Int, newValue: T)
}