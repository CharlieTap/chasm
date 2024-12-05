package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceFiller = (MemoryInstance, IntRange, Byte) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceFiller(
    instance: MemoryInstance,
    rangeToFill: IntRange,
    valueToWrite: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceFiller(
        instance = instance,
        rangeToFill = rangeToFill,
        valueToWrite = valueToWrite,
        linearMemoryInteractor = ::PessimisticBoundsChecker,
    )

internal inline fun MemoryInstanceFiller(
    instance: MemoryInstance,
    rangeToFill: IntRange,
    valueToWrite: Byte,
    linearMemoryInteractor: PessimisticBoundsChecker<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(
    rangeToFill.first,
    rangeToFill.last - rangeToFill.first,
    instance,
) {
    (instance.data as ByteArrayLinearMemory).memory.fill(valueToWrite, rangeToFill.first, rangeToFill.last)
}
