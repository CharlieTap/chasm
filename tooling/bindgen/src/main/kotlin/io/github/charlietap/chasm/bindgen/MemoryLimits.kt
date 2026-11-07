package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.config.StoreLimits

sealed interface MemoryLimits {
    class Unconstrained : MemoryLimits

    data class Constrained(
        val linearMemory: ByteSize = 256.MB,
        val gcHeap: ByteSize = 256.MB,
        val tables: ByteSize = 8.MB,
        val stack: ByteSize = 16.MB,
    ) : MemoryLimits {
        init {
            require(linearMemory.bytes / ByteSize.WASM_PAGE.bytes <= Int.MAX_VALUE) { "linearMemory is larger than Wasm allows" }
            require(stack.bytes in 1..MAX_STACK.bytes) { "stack must be between 1 byte and $MAX_STACK" }
        }

        internal val linearMemoryPages: Int get() = (linearMemory.bytes / ByteSize.WASM_PAGE.bytes).toInt()

        internal val tableElements: Int get() = (tables.bytes / TABLE_ELEMENT_BYTES).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    companion object {
        val MAX_STACK: ByteSize = 256.MB

        private const val TABLE_ELEMENT_BYTES = 8L
    }
}

fun MemoryLimits.toStoreLimits(): StoreLimits =
    when (this) {
        is MemoryLimits.Unconstrained -> StoreLimits()
        is MemoryLimits.Constrained -> StoreLimits(
            gcHeapBytes = gcHeap.bytes,
            memoryPages = linearMemoryPages,
            tableElements = tableElements,
            trapOnGrowFailure = true,
            stackBytes = stack.bytes,
        )
    }
