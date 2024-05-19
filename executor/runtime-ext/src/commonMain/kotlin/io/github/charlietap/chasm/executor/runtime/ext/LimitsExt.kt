@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.Limits

inline fun Limits.asRange() = min..(max ?: UInt.MAX_VALUE)
