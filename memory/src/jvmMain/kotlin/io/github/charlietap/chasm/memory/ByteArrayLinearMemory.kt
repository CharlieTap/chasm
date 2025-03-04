package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

data class ByteArrayLinearMemory(
    var memory: ByteArray,
) : LinearMemory {

    constructor(
        pages: LinearMemory.Pages,
    ) : this(ByteArray(pages.amount.toInt() * LinearMemory.PAGE_SIZE))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayLinearMemory) return false

        return memory.contentEquals(other.memory)
    }

    override fun hashCode(): Int {
        return memory.contentHashCode()
    }
}
