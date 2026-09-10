package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.ByteArrayHostMemory
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.memory.ext.copyInto
import io.github.charlietap.chasm.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@OptIn(UnsafeHostApi::class)
class ByteArrayLinearMemory(
    var memory: ByteArray,
) : LinearMemory, ByteArrayHostMemory {
    constructor(
        pages: LinearMemory.Pages,
    ) : this(
        ByteArray(pages.amount.toInt() * LinearMemory.PAGE_SIZE),
    )

    override val byteSize: Int
        get() = memory.size

    override fun grow(pagesToAdd: Int): LinearMemory {
        memory = memory.copyOf(memory.size + (pagesToAdd * LinearMemory.PAGE_SIZE))
        return this
    }

    override fun readI8(memoryPointer: Int): Byte = memory[memoryPointer]

    override fun readI16(memoryPointer: Int): Short = memory.toShortLittleEndian(memoryPointer)

    override fun readI32(memoryPointer: Int): Int = memory.toIntLittleEndian(memoryPointer)

    override fun readI64(memoryPointer: Int): Long = memory.toLongLittleEndian(memoryPointer)

    override fun readF32(memoryPointer: Int): Float = memory.toFloatLittleEndian(memoryPointer)

    override fun readF64(memoryPointer: Int): Double = memory.toDoubleLittleEndian(memoryPointer)

    override fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        memory.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
        return buffer
    }

    override fun writeI8(memoryPointer: Int, value: Byte) {
        memory[memoryPointer] = value
    }

    override fun writeI16(memoryPointer: Int, value: Short) {
        memory[memoryPointer] = (value.toInt() and 0xFF).toByte()
        memory[memoryPointer + 1] = (value.toInt() shr 8 and 0xFF).toByte()
    }

    override fun writeI32(memoryPointer: Int, value: Int) {
        value.copyInto(memory, memoryPointer)
    }

    override fun writeI64(memoryPointer: Int, value: Long) {
        value.copyInto(memory, memoryPointer)
    }

    override fun writeF32(memoryPointer: Int, value: Float) {
        value.copyInto(memory, memoryPointer)
    }

    override fun writeF64(memoryPointer: Int, value: Double) {
        value.copyInto(memory, memoryPointer)
    }

    override fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        buffer.copyInto(memory, memoryPointer, bufferPointer, bufferPointer + bytesToWrite)
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) {
        memory.fill(value, memoryPointer, memoryPointer + bytesToFill)
    }

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) {
        val sourceArray = (source as ByteArrayHostMemory).unsafeBorrowByteArray()
        sourceArray.copyInto(memory, destinationPointer, sourcePointer, sourcePointer + bytesToCopy)
    }

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) {
        val sourceArray = (source as ByteArrayHostMemory).unsafeBorrowByteArray()
        sourceArray.copyInto(memory, destinationPointer, sourcePointer, sourcePointer + bytesToMove)
    }

    @UnsafeHostApi
    override fun unsafeBorrowByteArray(): ByteArray = memory
}
