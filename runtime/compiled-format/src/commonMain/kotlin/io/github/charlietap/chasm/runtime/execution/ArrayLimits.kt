package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
const val ARRAY_OVERHEAD = 32

@InternalChasmApi
fun maximumArrayLength(shift: Int): Int {
    return (Int.MAX_VALUE - ARRAY_OVERHEAD) ushr shift
}
