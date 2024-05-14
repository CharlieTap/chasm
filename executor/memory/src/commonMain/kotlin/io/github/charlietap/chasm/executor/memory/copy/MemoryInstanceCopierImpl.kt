@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceCopierImpl(
    instance: MemoryInstance,
    srcRange: IntRange,
    dstOffset: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceCopierImpl(
        instance = instance,
        srcRange = srcRange,
        dstOffset = dstOffset,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceCopierImpl(
    instance: MemoryInstance,
    srcRange: IntRange,
    dstOffset: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(
    instance.data,
    dstOffset,
    srcRange.last - srcRange.first,
) {
    val memory = (instance.data as ByteArrayLinearMemory).memory
    memory.copyInto(memory, dstOffset, srcRange.first, srcRange.last)
}
