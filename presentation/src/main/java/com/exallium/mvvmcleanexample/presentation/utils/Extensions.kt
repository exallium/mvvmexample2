package com.exallium.mvvmcleanexample.presentation.utils

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ViewDataBinding
import com.exallium.mvvmcleanexample.presentation.utils.delegates.*
import com.exallium.mvvmcleanexample.presentation.utils.delegates.BindableProperty.Companion.USE_PROPERTY_CACHER
import com.exallium.mvvmcleanexample.presentation.utils.notifiable.NotifiableObservable
import com.jakewharton.rxrelay2.Relay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias OnChange<T> = (T) -> Unit

/**
 * Convenience method for creating a [DistinctObservableProperty].
 * @param onChange an optional block that will be executed when the property has changed.
 * @param onChangeOnInit if true, the provided [onChange] block will be executed with the [initialValue]. The default is true.
 *
 */
fun <T> distinctObservable(
        initialValue: T,
        onChange: OnChange<T>? = null,
        onChangeOnInit: Boolean = true):
        ReadWriteProperty<Any?, T> = object : DistinctObservableProperty<T>(initialValue) {

    init {
        if (onChangeOnInit)
            onChange?.invoke(initialValue)
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        onChange?.invoke(newValue)
    }
}

/**
 * Creates a [BindableProperty] for properties of classes that extend [BaseObservable].
 *
 * The property must be annotated with [Bindable].
 *
 * Usage with all parameters:
 * ```
 * @get:Bindable
 * var firstName: String by bindable("", BR.firstName)
 * ```
 *
 * Usage without explicit resource ID and type inference:
 * ```
 * @get:Bindable
 * var firstName by bindable("")
 *
 * ```
 * @param bindableResourceId the id from the generated `BR` class. If omitted, the [PropertyCacher] must be initialised prior to usage.
 */
fun <T> BaseObservable.bindable(
        initialValue: T,
        bindableResourceId: Int = USE_PROPERTY_CACHER)
        : BindableProperty<T, NotifiableObservable> {

    return object : BindableProperty<T, NotifiableObservable>(initialValue, bindableResourceId) {
        override fun notify(observable: NotifiableObservable, propertyId: Int, newValue: T) {
            observable.notifyPropertyChanged(propertyId)
        }
    }
}

/**
 * Creates a [BindableProperty] for properties of classes that extend [BaseObservable].
 *
 * The property must be annotated with [Bindable].
 *
 * Usage with all parameters:
 * ```
 * @get:Bindable
 * var firstName: String by bindable("", BR.firstName) {
 *      // do something
 * }
 * ```
 *
 * Usage without explicit resource ID and type inference:
 * ```
 * @get:Bindable
 * var firstName by bindable("") {
 *      // do something
 * }
 *
 * ```
 * @param bindableResourceId the id from the generated `BR` class. If omitted, the [PropertyCacher] must be initialised prior to usage.
 * @param onChange an optional block that will be executed after the property has changed, executed also on the initial value.
 * @param onChangeOnInit if true, the provided [onChange] block will be executed with the [initialValue].
 */
inline fun <T> BaseObservable.bindable(
        initialValue: T,
        bindableResourceId: Int = USE_PROPERTY_CACHER,
        crossinline onChange: OnChange<T>,
        onChangeOnInit: Boolean = true)
        : BindableProperty<T, NotifiableObservable> {

    return object : BindableProperty<T, NotifiableObservable>(initialValue, bindableResourceId) {

        init {
            if (onChangeOnInit)
                onChange.invoke(initialValue)
        }

        override fun notify(observable: NotifiableObservable, propertyId: Int, newValue: T) {
            observable.notifyPropertyChanged(propertyId)
            onChange.invoke(newValue)
        }
    }
}

/**
 * Creates a [BindableProperty] for properties of classes that implement [NotifiableObservable].
 *
 * The property must be annotated with [Bindable].
 *
 * Usage with all parameters:
 * ```
 * @get:Bindable
 * var firstName: String by bindable("", BR.firstName)
 * ```
 *
 * Usage without explicit resource ID and type inference:
 * ```
 * @get:Bindable
 * var firstName by bindable("")
 *
 * ```
 * @param bindableResourceId the id from the generated `BR` class. If omitted, the [PropertyCacher] must be initialised prior to usage.
 */
fun <T> NotifiableObservable.bindable(
        initialValue: T,
        bindableResourceId: Int = USE_PROPERTY_CACHER)
        : BindableProperty<T, NotifiableObservable> {

    return object : BindableProperty<T, NotifiableObservable>(initialValue, bindableResourceId) {
        override fun notify(observable: NotifiableObservable, propertyId: Int, newValue: T) {
            observable.notifyPropertyChanged(propertyId)
        }
    }
}

/**
 * Creates a [BindableProperty] for properties of classes that implement [NotifiableObservable].
 *
 * The property must be annotated with [Bindable].
 *
 * Usage with all parameters:
 * ```
 * @get:Bindable
 * var firstName: String by bindable("", BR.firstName) {
 *      // do something
 * }
 * ```
 *
 * Usage without explicit resource ID and type inference:
 * ```
 * @get:Bindable
 * var firstName by bindable("") {
 *      // do something
 * }
 *
 * ```
 * @param bindableResourceId the id from the generated `BR` class. If omitted, the [PropertyCacher] must be initialised prior to usage.
 * @param onChange an optional block that will be executed after the property has changed, executed also on the initial value.
 * @param onChangeOnInit if true, the provided [onChange] block will be executed with the [initialValue].
 */
inline fun <T> NotifiableObservable.bindable(
        initialValue: T,
        bindableResourceId: Int = USE_PROPERTY_CACHER,
        crossinline onChange: OnChange<T>,
        onChangeOnInit: Boolean = true)
        : BindableProperty<T, NotifiableObservable> {

    return object : BindableProperty<T, NotifiableObservable>(initialValue, bindableResourceId) {

        init {
            if (onChangeOnInit)
                onChange.invoke(initialValue)
        }

        override fun notify(observable: NotifiableObservable, propertyId: Int, newValue: T) {
            observable.notifyPropertyChanged(propertyId)
            onChange.invoke(newValue)
        }
    }
}

/**
 * Convenience method for creating a [DisposableProperty]
 */
fun disposable(disposable: Disposable?): DisposableProperty {
    return DisposableProperty(disposable)
}

/**
 * Convenience method for creating a [DisposableProperty]
 */
fun CompDisposable(): CompositeDisposableProperty {
    return CompositeDisposableProperty(CompositeDisposable())
}

/**
 * Creates a [ViewBindingProperty] with the given [ViewDataBinding].
 */
fun <Binding : ViewDataBinding> binding(binding: Binding?, vararg propertyIds: Int): ViewBindingProperty<Binding> {
    return ViewBindingProperty(binding, propertyIds.asList())
}

/**
 * Creates a [ViewBindingProperty] with the given [ViewDataBinding].
 */
fun <Binding : ViewDataBinding> autoViewBinding(binding: Binding?): ViewBindingProperty<Binding> {
    PropertyCacher.preCache
    return ViewBindingProperty(binding, PropertyCacher.resourceIds)
}

/**
 * Calls [ViewDataBinding.unbind] followed by [ViewDataBinding.executePendingBindings]
 */
fun ViewDataBinding.unbindNow() {
    unbind()
    executePendingBindings()
}


operator fun CompositeDisposable?.plusAssign(other: Disposable) {
    this?.add(other)
}

operator fun CompositeDisposable?.minusAssign(other: Disposable) {
    this?.remove(other)
}

inline fun <T> Relay<T>.accept(valueSupplier: () -> T) {
    accept(valueSupplier())
}
