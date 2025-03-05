package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.type.Limits

inline fun Limits.asRange() = min..(max ?: UInt.MAX_VALUE)
