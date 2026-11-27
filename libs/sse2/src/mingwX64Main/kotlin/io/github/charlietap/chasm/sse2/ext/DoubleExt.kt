package io.github.charlietap.chasm.sse2.ext

import io.github.charlietap.chasm.InternalChasmApi
import libsse2.sse2_sqrt

@InternalChasmApi
actual inline fun Double.fsqrt() = sse2_sqrt(this)
