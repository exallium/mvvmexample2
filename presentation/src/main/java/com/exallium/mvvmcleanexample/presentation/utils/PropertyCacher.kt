package com.exallium.mvvmcleanexample.presentation.utils

import android.app.Application
import com.exallium.mvvmcleanexample.presentation.utils.PropertyCacher.bindableResourceClass
import kotlin.reflect.KProperty

/**
 * Provides a hassle free way of mapping a [KProperty] to an id from a generated `BR` databinding classes.
 *
 * To use, initialise the [bindableResourceClass] once in your [Application] like so:
 * ```
 * override fun onCreate() {
 *   super.onCreate()
 *   PropertyCacher.bindableResourceClass = BR::class.java
 * }
 *
 * ```
 */
object PropertyCacher {
    lateinit var bindableResourceClass: Class<*>
    private val resourceIdMap: HashMap<String, Int> = HashMap<String, Int>()

    val String.bindableResourceId: Int get() {
        return bindableResourceClass.let {
            try {
                val field = it.getDeclaredField(this)
                field.getInt(it)
            } catch (e: Throwable) {
                null
            }
        }!!
    }

    internal val preCache: Unit by lazy {
        bindableResourceClass.let {
            it.fields.forEach { field ->
                resourceIdMap.put(field.name, field.getInt(it))
            }
        }
    }

    fun bindableResourceIdForKProperty(property: KProperty<*>): Int {
        return resourceIdMap.getOrPut(property.name) { property.name.bindableResourceId }
    }


    internal val resourceIds: List<Int> get() = resourceIdMap.values.toList()
}