package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal fun ByteArrayLinearMemoryFactory(
    min: Int,
    max: Int?,
): LinearMemory = max?.let {
    ByteArrayLinearMemory(LinearMemory.Pages(min), LinearMemory.Pages(max))
} ?: ByteArrayLinearMemory(LinearMemory.Pages(min))
