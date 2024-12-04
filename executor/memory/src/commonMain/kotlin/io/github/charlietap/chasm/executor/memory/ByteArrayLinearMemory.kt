package io.github.charlietap.chasm.executor.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal data class ByteArrayLinearMemory(
    internal val memory: ByteArray,
) : LinearMemory {

    constructor(
        pages: LinearMemory.Pages,
    ) : this(ByteArray(pages.amount * LinearMemory.PAGE_SIZE))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayLinearMemory) return false

        return memory.contentEquals(other.memory)
    }

    override fun hashCode(): Int {
        return memory.contentHashCode()
    }
}
