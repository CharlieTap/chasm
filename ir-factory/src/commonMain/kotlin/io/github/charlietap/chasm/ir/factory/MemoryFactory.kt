package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.MemoryIndex
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ir.module.Index.MemoryIndex as IRMemoryIndex
import io.github.charlietap.chasm.ir.module.Memory as IRMemory
import io.github.charlietap.chasm.ir.type.MemoryType as IRMemoryType

internal fun MemoryFactory(
    memory: Memory,
): IRMemory = MemoryFactory(
    memory = memory,
    memoryIndexFactory = ::MemoryIndexFactory,
    memoryTypeFactory = ::MemoryTypeFactory,
)

internal inline fun MemoryFactory(
    memory: Memory,
    memoryIndexFactory: IRFactory<MemoryIndex, IRMemoryIndex>,
    memoryTypeFactory: IRFactory<MemoryType, IRMemoryType>,
): IRMemory {
    return IRMemory(
        idx = memoryIndexFactory(memory.idx),
        type = memoryTypeFactory(memory.type),
    )
}
