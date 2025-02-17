package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.executor.runtime.memory.linearMemory
import io.github.charlietap.chasm.fixture.ir.type.memoryType
import io.github.charlietap.chasm.ir.type.MemoryType

fun memoryInstance(
    type: MemoryType = memoryType(),
    data: LinearMemory = linearMemory(),
) = MemoryInstance(
    type = type,
    data = data,
)
