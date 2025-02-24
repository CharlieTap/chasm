package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.type.MemoryType

data class Memory(
    val idx: Index.MemoryIndex,
    val type: MemoryType,
)
