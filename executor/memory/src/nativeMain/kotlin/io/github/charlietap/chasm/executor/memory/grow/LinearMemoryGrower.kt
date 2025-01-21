package io.github.charlietap.chasm.executor.memory.grow

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.grow

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory {
    val nativeMemory = memory as NativeLinearMemory
    return if (grow(nativeMemory.pointer, pagesToAdd.toUInt())) {
        nativeMemory
    } else {
        throw InvocationException(InvocationError.MemoryGrowFailed)
    }
}
