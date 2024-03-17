package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

fun LinearMemoryFactoryImpl(
    min: Int,
    max: Int?,
): LinearMemory = max?.let {
    ByteArrayLinearMemory(LinearMemory.Pages(min), LinearMemory.Pages(max))
} ?: ByteArrayLinearMemory(LinearMemory.Pages(min))
