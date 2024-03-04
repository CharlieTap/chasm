package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.fixture.type.memoryType

fun memory(
    idx: Index.MemoryIndex = Index.MemoryIndex(0u),
    type: MemoryType = memoryType(),
) = Memory(
    idx = idx,
    type = type,
)
