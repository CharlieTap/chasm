@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import kotlin.math.sqrt

internal inline fun Double.eq(other: Double): Boolean = this == other

internal inline fun Double.eqz(): Boolean = this == 0.0

internal inline fun Double.sqrt(): Double = sqrt(this)
