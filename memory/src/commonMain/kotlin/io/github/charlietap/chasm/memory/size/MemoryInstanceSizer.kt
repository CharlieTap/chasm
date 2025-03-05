package io.github.charlietap.chasm.memory.size

import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias MemoryInstanceSizer = (MemoryInstance) -> Int

fun MemoryInstanceSizerImpl(
    instance: MemoryInstance,
): Int = instance.type.limits.min
    .toInt() * LinearMemory.PAGE_SIZE
