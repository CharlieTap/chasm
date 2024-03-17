package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.grow
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

fun MemoryGrowerImpl(
    memoryInstance: MemoryInstance,
    pagesToAdd: Int,
): Result<MemoryInstance, InvocationError.MemoryGrowExceedsLimits> = binding {

    val newData = (memoryInstance.data as ByteArrayLinearMemory).grow(LinearMemory.Pages(pagesToAdd)).bind()

    val newLimits = memoryInstance.type.limits.copy(
        min = memoryInstance.type.limits.min + pagesToAdd.toUInt(),
    )
    val newType = MemoryType(newLimits)

    MemoryInstance(newType, newData)
}
