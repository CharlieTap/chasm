package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I64ToI32Writer = (LinearMemory, Int, Long) -> Unit

@InternalChasmApi
expect inline fun I64ToI32Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
)
