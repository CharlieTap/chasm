package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

data class MemoryInstance(
    val type: MemoryType,
    val data: LinearMemory,
)
