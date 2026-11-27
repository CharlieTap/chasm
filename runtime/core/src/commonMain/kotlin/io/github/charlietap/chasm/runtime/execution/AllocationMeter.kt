package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmField

@InternalChasmApi
class AllocationMeter {

    @JvmField
    var remaining: Int = ALLOCATION_INTERVAL

    @JvmField
    var counted: Long = 0

    val allocatedBytes: Long
        get() = counted + (ALLOCATION_INTERVAL - remaining)
}

@InternalChasmApi
const val ALLOCATION_INTERVAL = 1 shl 20
