@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceByteWriterImpl(
    instance: MemoryInstance,
    pointer: Int,
    byte: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceByteWriterImpl(
        instance = instance,
        pointer = pointer,
        byte = byte,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceByteWriterImpl(
    instance: MemoryInstance,
    pointer: Int,
    byte: Byte,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, pointer, 1) {
    (instance.data as ByteArrayLinearMemory).memory[pointer] = byte
}
