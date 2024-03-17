package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.memory.linearMemory
import io.github.charlietap.chasm.fixture.type.memoryType

fun memoryInstance(
    type: MemoryType = memoryType(),
    data: LinearMemory = linearMemory(),
) = MemoryInstance(
    type = type,
    data = data,
)
