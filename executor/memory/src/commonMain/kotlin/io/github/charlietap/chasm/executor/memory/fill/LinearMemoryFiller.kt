@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryFiller = (LinearMemory, Int, Int, Byte) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

expect inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds>
