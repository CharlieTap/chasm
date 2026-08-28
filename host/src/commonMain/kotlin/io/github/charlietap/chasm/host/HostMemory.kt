package io.github.charlietap.chasm.host

/**
 * `HostMemory` is an API for reading and changing a WebAssembly linear memory
 * inside host functions.
 */
interface HostMemory {

    val byteSize: Int

    fun readI8(memoryPointer: Int): Byte

    fun readI16(memoryPointer: Int): Short

    fun readI32(memoryPointer: Int): Int

    fun readI64(memoryPointer: Int): Long

    fun readF32(memoryPointer: Int): Float

    fun readF64(memoryPointer: Int): Double

    fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int = 0,
    ): ByteArray

    fun writeI8(memoryPointer: Int, value: Byte)

    fun writeI16(memoryPointer: Int, value: Short)

    fun writeI32(memoryPointer: Int, value: Int)

    fun writeI64(memoryPointer: Int, value: Long)

    fun writeF32(memoryPointer: Int, value: Float)

    fun writeF64(memoryPointer: Int, value: Double)

    fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int = 0,
        bytesToWrite: Int = buffer.size - bufferPointer,
    )

    fun fill(
        memoryPointer: Int,
        value: Byte,
        bytesToFill: Int,
    )

    /** Copies between non-overlapping ranges. */
    fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory = this,
    )

    /** Copies between ranges that may overlap. */
    fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory = this,
    )
}
