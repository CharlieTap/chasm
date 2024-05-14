@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceFillerImpl(
    instance: MemoryInstance,
    rangeToFill: IntRange,
    valueToWrite: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceFillerImpl(
        instance = instance,
        rangeToFill = rangeToFill,
        valueToWrite = valueToWrite,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceFillerImpl(
    instance: MemoryInstance,
    rangeToFill: IntRange,
    valueToWrite: Byte,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(
    instance.data,
    rangeToFill.first,
    rangeToFill.last - rangeToFill.first,
) {
    (instance.data as ByteArrayLinearMemory).memory.fill(valueToWrite, rangeToFill.first, rangeToFill.last)
}
