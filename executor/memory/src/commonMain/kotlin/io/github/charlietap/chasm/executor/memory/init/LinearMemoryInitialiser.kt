@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.init

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryInitialiser = (UByteArray, LinearMemory, Int, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

expect inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds>
