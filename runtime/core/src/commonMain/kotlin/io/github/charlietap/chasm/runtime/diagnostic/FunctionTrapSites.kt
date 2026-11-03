package io.github.charlietap.chasm.runtime.diagnostic

import io.github.charlietap.chasm.InternalChasmApi

/**
 * Where each compiled instruction of a function came from in the Wasm
 * binary. [chunkStarts] holds the first compiled instruction of each chunk
 * method when the function was split, and is empty otherwise.
 */
@InternalChasmApi
class FunctionTrapSites(
    val chunkStarts: IntArray,
    private val offsets: IntArray,
    private val memories: IntArray = IntArray(0),
) {
    val size: Int get() = offsets.size

    /** The Wasm offset of the compiled instruction at [position], or -1. */
    fun offset(
        position: Int,
    ): Int = if (position in offsets.indices) offsets[position] else -1

    /**
     * The memory index the memory instruction at [position] accesses, or -1.
     * A copy names its source when [source] is true and its destination
     * otherwise.
     */
    fun memoryIndex(
        position: Int,
        source: Boolean,
    ): Int {
        val packed = packedMemories(position)
        val first = (packed and 0xFFFF) - 1
        val second = (packed ushr 16) - 1
        return if (second >= 0 && !source) second else first
    }

    fun withChunkStarts(
        chunkStarts: IntArray,
    ): FunctionTrapSites = FunctionTrapSites(chunkStarts, offsets, memories)

    fun packedMemories(
        position: Int,
    ): Int = if (position in memories.indices) memories[position] else 0
}
