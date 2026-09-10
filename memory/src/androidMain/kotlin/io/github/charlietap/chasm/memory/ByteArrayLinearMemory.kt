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

private const val MAX_ANDROID_MEMORY_PAGES = Int.MAX_VALUE / LinearMemory.PAGE_SIZE

@OptIn(UnsafeHostApi::class)
class ByteArrayLinearMemory(
    pages: LinearMemory.Pages,
    val maximumPages: Int = MAX_ANDROID_MEMORY_PAGES,
) : LinearMemory, ByteArrayHostMemory {
    override var byteSize: Int = checkedInitialSize(pages, maximumPages)
        private set

    @PublishedApi
    internal var bytes: ByteArray = allocateMemoryArray(
        initialArrayCapacity(byteSize, maximumPages),
        byteSize,
    )
        private set

    override fun grow(pagesToAdd: Int): LinearMemory {
        val newSize = checkedGrowthSize(pagesToAdd) ?: throw OutOfMemoryError("Memory growth exceeds limits")
        if (newSize != byteSize) growStorage(newSize)
        return this
    }

    internal fun checkedGrowthSize(pagesToAdd: Int): Int? {
        if (pagesToAdd < 0) return null
        val newByteSize = byteSize.toLong() + pagesToAdd.toLong() * LinearMemory.PAGE_SIZE
        return if (newByteSize <= maximumPages.toLong() * LinearMemory.PAGE_SIZE) newByteSize.toInt() else null
    }

    internal fun growStorage(newSize: Int) = growStorage(newSize, ::ByteArray)

    internal fun growStorage(newSize: Int, allocate: (Int) -> ByteArray) {
        if (newSize > bytes.size) {
            val capacity = grownArrayCapacity(bytes.size, newSize, maximumPages)
            // All fallible work precedes publication. On failure the old array,
            // logical size and existing host views remain usable.
            val newBytes = allocateMemoryArray(capacity, newSize, allocate)
            bytes.copyInto(newBytes, 0, 0, byteSize)
            bytes = newBytes
        }
        // Spare bytes were zeroed at allocation and remain outside logical memory.
        byteSize = newSize
    }

    internal fun release() {
        bytes = ByteArray(0)
        byteSize = 0
    }

    private fun checkedIndex(address: Int, width: Int): Int {
        BoundsChecker(address, width, byteSize)
        return address
    }

    override fun readI8(memoryPointer: Int): Byte {
        val index = checkedIndex(memoryPointer, Byte.SIZE_BYTES)
        return bytes[index]
    }

    override fun writeI8(memoryPointer: Int, value: Byte) {
        val index = checkedIndex(memoryPointer, Byte.SIZE_BYTES)
        bytes[index] = value
    }

    override fun readI16(memoryPointer: Int): Short {
        val index = checkedIndex(memoryPointer, Short.SIZE_BYTES)
        return bytes.toShortLittleEndian(index)
    }

    override fun writeI16(memoryPointer: Int, value: Short) {
        val index = checkedIndex(memoryPointer, Short.SIZE_BYTES)
        bytes[index] = value.toByte()
        bytes[index + 1] = (value.toInt() ushr 8).toByte()
    }

    override fun readI32(memoryPointer: Int): Int {
        val index = checkedIndex(memoryPointer, Int.SIZE_BYTES)
        return bytes.toIntLittleEndian(index)
    }

    override fun writeI32(memoryPointer: Int, value: Int) {
        val index = checkedIndex(memoryPointer, Int.SIZE_BYTES)
        value.copyInto(bytes, index)
    }

    override fun readI64(memoryPointer: Int): Long {
        val index = checkedIndex(memoryPointer, Long.SIZE_BYTES)
        return bytes.toLongLittleEndian(index)
    }

    override fun writeI64(memoryPointer: Int, value: Long) {
        val index = checkedIndex(memoryPointer, Long.SIZE_BYTES)
        value.copyInto(bytes, index)
    }

    override fun readF32(memoryPointer: Int): Float {
        val index = checkedIndex(memoryPointer, Float.SIZE_BYTES)
        return bytes.toFloatLittleEndian(index)
    }

    override fun writeF32(memoryPointer: Int, value: Float) {
        val index = checkedIndex(memoryPointer, Float.SIZE_BYTES)
        value.copyInto(bytes, index)
    }

    override fun readF64(memoryPointer: Int): Double {
        val index = checkedIndex(memoryPointer, Double.SIZE_BYTES)
        return bytes.toDoubleLittleEndian(index)
    }

    override fun writeF64(memoryPointer: Int, value: Double) {
        val index = checkedIndex(memoryPointer, Double.SIZE_BYTES)
        value.copyInto(bytes, index)
    }

    override fun read(buffer: ByteArray, memoryPointer: Int, bytesToRead: Int, bufferPointer: Int): ByteArray {
        BoundsChecker(memoryPointer, bytesToRead, byteSize)
        BoundsChecker(bufferPointer, bytesToRead, buffer.size)
        bytes.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
        return buffer
    }

    override fun write(memoryPointer: Int, buffer: ByteArray, bufferPointer: Int, bytesToWrite: Int) {
        BoundsChecker(memoryPointer, bytesToWrite, byteSize)
        BoundsChecker(bufferPointer, bytesToWrite, buffer.size)
        buffer.copyInto(bytes, memoryPointer, bufferPointer, bufferPointer + bytesToWrite)
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) {
        BoundsChecker(memoryPointer, bytesToFill, byteSize)
        var filled = minOf(64, bytesToFill)
        bytes.fill(value, memoryPointer, memoryPointer + filled)
        while (filled < bytesToFill) {
            val count = minOf(filled, bytesToFill - filled)
            bytes.copyInto(bytes, memoryPointer + filled, memoryPointer, memoryPointer + count)
            filled += count
        }
    }

    override fun copy(sourcePointer: Int, destinationPointer: Int, bytesToCopy: Int, source: HostMemory) {
        BoundsChecker(sourcePointer, bytesToCopy, source.byteSize)
        BoundsChecker(destinationPointer, bytesToCopy, byteSize)
        if (source is ByteArrayHostMemory) {
            source.unsafeBorrowByteArray().copyInto(
                bytes,
                destinationPointer,
                sourcePointer,
                sourcePointer + bytesToCopy,
            )
        } else {
            source.read(bytes, sourcePointer, bytesToCopy, destinationPointer)
        }
    }

    override fun move(sourcePointer: Int, destinationPointer: Int, bytesToMove: Int, source: HostMemory) {
        copy(sourcePointer, destinationPointer, bytesToMove, source)
    }

    @UnsafeHostApi
    override fun unsafeBorrowByteArray(): ByteArray = bytes

    private companion object {
        fun checkedInitialSize(pages: LinearMemory.Pages, maximumPages: Int): Int {
            if (
                maximumPages < 0 ||
                maximumPages > MAX_ANDROID_MEMORY_PAGES ||
                pages.amount.toULong() > maximumPages.toULong()
            ) {
                throw OutOfMemoryError("Linear memory exceeds Android array capacity")
            }
            return (pages.amount.toLong() * LinearMemory.PAGE_SIZE).toInt()
        }
    }
}
