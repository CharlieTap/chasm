package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.MemoryIndex
import io.github.charlietap.chasm.type.MemoryType

data class Memory(
    val idx: MemoryIndex,
    val type: MemoryType,
)
