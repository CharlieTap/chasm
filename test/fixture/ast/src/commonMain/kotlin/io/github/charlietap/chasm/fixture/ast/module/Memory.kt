package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.type.MemoryType

fun memory(
    idx: Index.MemoryIndex = Index.MemoryIndex(0u),
    type: MemoryType = memoryType(),
) = Memory(
    idx = idx,
    type = type,
)
