package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
inline fun IntRange.contains(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}
