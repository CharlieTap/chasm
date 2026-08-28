package io.github.charlietap.chasm.host

/**
 * `HostTable` is an API for reading and changing the raw references in a
 * WebAssembly table inside host functions.
 */
interface HostTable {

    val size: Int

    fun readRaw(index: Int): Long

    fun writeRaw(index: Int, value: Long)

    fun read(
        buffer: LongArray,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int = 0,
    ): LongArray

    fun write(
        elementIndex: Int,
        buffer: LongArray,
        bufferIndex: Int = 0,
        elementsToWrite: Int = buffer.size - bufferIndex,
    )

    fun fill(
        elementIndex: Int,
        value: HostReference,
        elementsToFill: Int,
    )

    /** Copies between non-overlapping ranges. */
    fun copy(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToCopy: Int,
        source: HostTable = this,
    )

    /** Copies between ranges that may overlap. */
    fun move(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToMove: Int,
        source: HostTable = this,
    )

    /**
     * Borrows the table's real backing array. It must not be retained across
     * table growth, deallocation, or re-entry that can replace the backing.
     */
    @UnsafeHostApi
    fun unsafeBorrowElements(): LongArray
}
