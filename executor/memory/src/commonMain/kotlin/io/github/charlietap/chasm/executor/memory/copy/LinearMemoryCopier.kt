package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryCopier = (LinearMemory, LinearMemory, Int, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

expect inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds>
