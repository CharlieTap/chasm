package io.github.charlietap.chasm.runtime.execution

const val ARRAY_OVERHEAD = 32

fun maximumArrayLength(shift: Int): Int {
    return (Int.MAX_VALUE - ARRAY_OVERHEAD) ushr shift
}
