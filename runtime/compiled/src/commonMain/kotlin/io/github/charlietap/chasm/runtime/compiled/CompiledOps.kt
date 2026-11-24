@file:JvmName("CompiledOps")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import kotlin.jvm.JvmName

fun memorySize(
    memory: MemoryInstance,
): Long = memory.type.limits.min.toLong()

fun memoryGrow(
    context: ExecutionContext,
    memory: MemoryInstance,
    pages: Int,
): Long {
    val previous = memory.grow(pages)
    if (previous < 0 && context.store.trapOnGrowthFailure) {
        throw InvocationException(InvocationError.MemoryGrowFailed)
    }
    return previous.toLong()
}

fun memoryInit(
    memory: MemoryInstance,
    data: DataInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) = try {
    LinearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, count, data.bytes.size, memory.size)
} catch (exception: InvocationException) {
    // The segment's range is checked first; an invalid one is not a memory access.
    throw if (unsigned(sourceOffset) + unsigned(count) > data.bytes.size) {
        exception
    } else {
        accessFailed(exception, FailedMemoryAccess.Kind.INIT, memory, unsigned(destinationOffset), unsigned(count))
    }
}

fun dataDrop(
    data: DataInstance,
) {
    data.bytes = DataInstance.EMPTY
}
