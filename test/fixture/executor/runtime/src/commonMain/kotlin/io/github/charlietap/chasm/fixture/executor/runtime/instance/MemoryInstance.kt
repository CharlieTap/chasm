package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.executor.runtime.memory.linearMemory

fun memoryInstance(
    type: MemoryType = memoryType(),
    data: LinearMemory = linearMemory(),
) = MemoryInstance(
    type = type,
    data = data,
)
