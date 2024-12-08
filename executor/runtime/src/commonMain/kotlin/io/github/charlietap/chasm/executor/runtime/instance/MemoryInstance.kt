@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE

data class MemoryInstance(
    val type: MemoryType,
    val data: LinearMemory,
) {
    val bounds by lazy {
        0..<size
    }
    val size by lazy {
        type.limits.min.toInt() * PAGE_SIZE
    }
}
