package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE

data class MemoryInstance(
    var type: MemoryType,
    var data: LinearMemory,
) {
    var size: Int = size()
        private set

    var bounds: IntRange = bounds()
        private set

    fun refresh() {
        size = size()
        bounds = bounds()
    }

    private fun size() = type.limits.min.toInt() * PAGE_SIZE

    private fun bounds() = 0 until size
}
