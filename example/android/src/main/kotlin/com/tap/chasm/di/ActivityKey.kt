package com.tap.chasm.di

import android.app.Activity
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.CLASS)
annotation class ActivityKey(val value: KClass<out Activity>)
