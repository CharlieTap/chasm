package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.type.MemoryType

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
