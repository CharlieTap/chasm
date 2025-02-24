package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Memory
import io.github.charlietap.chasm.type.MemoryType

fun memory(
    idx: Index.MemoryIndex = memoryIndex(),
    type: MemoryType = memoryType(),
) = Memory(
    idx = idx,
    type = type,
)
