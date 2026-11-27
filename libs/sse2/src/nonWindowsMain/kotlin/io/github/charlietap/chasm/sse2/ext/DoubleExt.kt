package io.github.charlietap.chasm.sse2.ext

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.math.sqrt

@InternalChasmApi
actual inline fun Double.fsqrt(): Double = sqrt(this)
