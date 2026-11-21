package io.github.charlietap.chasm.runtime.execution

import kotlin.jvm.JvmField

class AllocationMeter {

    @JvmField
    var remaining: Int = ALLOCATION_INTERVAL

    @JvmField
    var counted: Long = 0

    val allocatedBytes: Long
        get() = counted + (ALLOCATION_INTERVAL - remaining)
}

const val ALLOCATION_INTERVAL = 1 shl 20

const val ARRAY_OVERHEAD = 32

fun maximumArrayLength(
    shift: Int,
): Int {
    return (Int.MAX_VALUE - ARRAY_OVERHEAD) ushr shift
}
