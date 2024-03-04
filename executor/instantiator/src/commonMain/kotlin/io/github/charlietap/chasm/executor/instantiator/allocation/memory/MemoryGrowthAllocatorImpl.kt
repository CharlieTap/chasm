package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal fun MemoryGrowthAllocatorAllocatorImpl(
    memoryInstance: MemoryInstance,
    pagesToAdd: Int,
): Result<MemoryInstance, InvocationError.MemoryGrowExceedsLimits> = binding {

    val newData = memoryInstance.data.grow(LinearMemory.Pages(pagesToAdd)).bind()

    val newLimits = memoryInstance.type.limits.copy(
        min = memoryInstance.type.limits.min + pagesToAdd.toUInt(),
    )
    val newType = MemoryType(newLimits)

    MemoryInstance(newType, newData)
}
