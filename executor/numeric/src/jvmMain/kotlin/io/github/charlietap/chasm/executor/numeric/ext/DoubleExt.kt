@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.numeric.ext

import kotlin.math.sqrt

actual inline fun Double.fsqrt(): Double = sqrt(this)
