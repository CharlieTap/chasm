@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import kotlin.math.sqrt

internal inline fun Float.eq(other: Float): Boolean = this == other

internal inline fun Float.eqz(): Boolean = this == 0f

internal inline fun Float.sqrt(): Float = sqrt(this)
