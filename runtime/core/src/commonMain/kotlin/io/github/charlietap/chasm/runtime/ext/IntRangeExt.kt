package io.github.charlietap.chasm.runtime.ext

inline fun IntRange.contains(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}
