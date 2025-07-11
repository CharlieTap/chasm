package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.decoder.section.MemorySection

internal fun memorySection(
    memories: List<Memory> = emptyList(),
) = MemorySection(memories)
