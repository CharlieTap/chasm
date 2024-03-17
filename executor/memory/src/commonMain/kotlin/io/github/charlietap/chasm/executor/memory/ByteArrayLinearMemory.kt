package io.github.charlietap.chasm.executor.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal data class ByteArrayLinearMemory(
    override val min: LinearMemory.Pages,
    override val max: LinearMemory.Pages,
    internal val memory: ByteArray,
) : LinearMemory {

    constructor(
        min: LinearMemory.Pages,
        max: LinearMemory.Pages = LinearMemory.Pages(LinearMemory.MAX_PAGES),
    ) : this(min, max, ByteArray(min.amount * LinearMemory.PAGE_SIZE))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ByteArrayLinearMemory

        if (min != other.min) return false
        if (max != other.max) return false
        if (!memory.contentEquals(other.memory)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + memory.contentHashCode()
        return result
    }
}
