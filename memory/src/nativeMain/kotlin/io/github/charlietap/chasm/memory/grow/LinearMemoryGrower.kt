package io.github.charlietap.chasm.memory.grow

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
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
