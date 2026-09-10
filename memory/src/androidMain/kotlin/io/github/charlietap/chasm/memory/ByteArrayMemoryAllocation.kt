package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

internal fun initialArrayCapacity(size: Int, maximumPages: Int): Int {
    val pages = size / LinearMemory.PAGE_SIZE
    val sparePages = if (pages == 0) 0 else minOf(16, maxOf(1, (pages + 7) / 8))
    return minOf(maximumPages, pages + sparePages) * LinearMemory.PAGE_SIZE
}

internal fun grownArrayCapacity(capacity: Int, required: Int, maximumPages: Int): Int {
    val pages = capacity / LinearMemory.PAGE_SIZE
    val extraPages = minOf(256, maxOf(1, (pages + 1) / 2))
    return minOf(maximumPages, maxOf(required / LinearMemory.PAGE_SIZE, pages + extraPages)) * LinearMemory.PAGE_SIZE
}

internal fun allocateMemoryArray(capacity: Int, required: Int, allocate: (Int) -> ByteArray = ::ByteArray): ByteArray =
    try {
        allocate(capacity)
    } catch (error: OutOfMemoryError) {
        // If reserving headroom fails, fall back to the required size and accept
        // more expensive future growth.
        if (capacity == required) throw error
        allocate(required)
    }
