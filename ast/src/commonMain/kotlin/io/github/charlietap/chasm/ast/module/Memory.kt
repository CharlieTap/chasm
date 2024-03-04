package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Index.MemoryIndex
import io.github.charlietap.chasm.ast.type.MemoryType

data class Memory(
    val idx: MemoryIndex,
    val type: MemoryType,
)
