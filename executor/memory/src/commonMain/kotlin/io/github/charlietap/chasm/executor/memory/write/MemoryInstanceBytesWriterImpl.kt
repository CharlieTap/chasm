@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceBytesWriterImpl(
    instance: MemoryInstance,
    pointer: Int,
    bytes: ByteArray,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceBytesWriterImpl(
        instance = instance,
        pointer = pointer,
        bytes = bytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceBytesWriterImpl(
    instance: MemoryInstance,
    pointer: Int,
    bytes: ByteArray,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, pointer, 1) {
    bytes.copyInto((instance.data as ByteArrayLinearMemory).memory, pointer)
}
