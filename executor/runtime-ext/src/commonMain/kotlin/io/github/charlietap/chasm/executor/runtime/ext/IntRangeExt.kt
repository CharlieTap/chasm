@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

inline fun IntRange.contains(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}
