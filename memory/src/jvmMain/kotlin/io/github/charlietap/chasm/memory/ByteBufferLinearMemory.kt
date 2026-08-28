package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.JvmHostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(UnsafeHostApi::class)
class ByteBufferLinearMemory(
    memory: ByteBuffer,
) : LinearMemory, JvmHostMemory {

    var memory: ByteBuffer = memory
        set(value) {
            field = value
            copySource = value.duplicate().order(value.order())
        }

    private var copySource = memory.duplicate().order(memory.order())

    constructor(
        pages: LinearMemory.Pages,
    ) : this(
        ByteBuffer.allocateDirect(pages.amount.toInt() * LinearMemory.PAGE_SIZE).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        },
    )

    override val byteSize: Int
        get() = memory.limit()

    override fun readI8(memoryPointer: Int): Byte = memory.get(memoryPointer)

    override fun readI16(memoryPointer: Int): Short = memory.getShort(memoryPointer)

    override fun readI32(memoryPointer: Int): Int = memory.getInt(memoryPointer)

    override fun readI64(memoryPointer: Int): Long = memory.getLong(memoryPointer)

    override fun readF32(memoryPointer: Int): Float = memory.getFloat(memoryPointer)

    override fun readF64(memoryPointer: Int): Double = memory.getDouble(memoryPointer)

    override fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        checkRange(memoryPointer, bytesToRead, memory.limit())
        checkRange(bufferPointer, bytesToRead, buffer.size)
        memory.position(memoryPointer)
        memory.get(buffer, bufferPointer, bytesToRead)
        return buffer
    }

    override fun writeI8(memoryPointer: Int, value: Byte) {
        memory.put(memoryPointer, value)
    }

    override fun writeI16(memoryPointer: Int, value: Short) {
        memory.putShort(memoryPointer, value)
    }

    override fun writeI32(memoryPointer: Int, value: Int) {
        memory.putInt(memoryPointer, value)
    }

    override fun writeI64(memoryPointer: Int, value: Long) {
        memory.putLong(memoryPointer, value)
    }

    override fun writeF32(memoryPointer: Int, value: Float) {
        memory.putFloat(memoryPointer, value)
    }

    override fun writeF64(memoryPointer: Int, value: Double) {
        memory.putDouble(memoryPointer, value)
    }

    override fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        checkRange(memoryPointer, bytesToWrite, memory.limit())
        checkRange(bufferPointer, bytesToWrite, buffer.size)
        memory.position(memoryPointer)
        memory.put(buffer, bufferPointer, bytesToWrite)
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) {
        checkRange(memoryPointer, bytesToFill, memory.limit())
        val repeatedValue = (value.toLong() and 0xFFL) * REPEATED_BYTE_MASK
        var index = 0
        while (index <= bytesToFill - UNROLLED_BYTES) {
            memory.putLong(memoryPointer + index, repeatedValue)
            memory.putLong(memoryPointer + index + 8, repeatedValue)
            memory.putLong(memoryPointer + index + 16, repeatedValue)
            memory.putLong(memoryPointer + index + 24, repeatedValue)
            index += UNROLLED_BYTES
        }
        while (index <= bytesToFill - Long.SIZE_BYTES) {
            memory.putLong(memoryPointer + index, repeatedValue)
            index += Long.SIZE_BYTES
        }
        while (index < bytesToFill) {
            memory.put(memoryPointer + index, value)
            index++
        }
    }

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) {
        val sourceBuffer = (source as JvmHostMemory).unsafeBorrowByteBuffer()
        checkRange(sourcePointer, bytesToCopy, sourceBuffer.limit())
        checkRange(destinationPointer, bytesToCopy, memory.limit())
        copy(sourceBuffer, sourcePointer, destinationPointer, bytesToCopy)
    }

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) {
        val sourceBuffer = (source as JvmHostMemory).unsafeBorrowByteBuffer()
        checkRange(sourcePointer, bytesToMove, sourceBuffer.limit())
        checkRange(destinationPointer, bytesToMove, memory.limit())

        if (
            sourceBuffer !== memory ||
            destinationPointer >= sourcePointer + bytesToMove ||
            sourcePointer >= destinationPointer + bytesToMove
        ) {
            copy(sourceBuffer, sourcePointer, destinationPointer, bytesToMove)
        } else if (destinationPointer > sourcePointer) {
            copyBackward(sourcePointer, destinationPointer, bytesToMove)
        } else if (destinationPointer < sourcePointer) {
            copyForward(sourceBuffer, sourcePointer, destinationPointer, bytesToMove)
        }
    }

    @UnsafeHostApi
    override fun unsafeBorrowByteBuffer(): ByteBuffer = memory

    private fun copy(
        source: ByteBuffer,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
    ) {
        if (bytesToCopy < BULK_COPY_THRESHOLD) {
            copyForward(source, sourcePointer, destinationPointer, bytesToCopy)
        } else if (source === memory) {
            copySource.limit(sourcePointer + bytesToCopy)
            copySource.position(sourcePointer)
            memory.position(destinationPointer)
            memory.put(copySource)
        } else {
            val sourceLimit = source.limit()
            source.limit(sourcePointer + bytesToCopy)
            source.position(sourcePointer)
            memory.position(destinationPointer)
            memory.put(source)
            source.limit(sourceLimit)
        }
    }

    private fun copyForward(
        source: ByteBuffer,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
    ) {
        var index = 0
        while (index <= bytesToCopy - UNROLLED_BYTES) {
            memory.putLong(destinationPointer + index, source.getLong(sourcePointer + index))
            memory.putLong(destinationPointer + index + 8, source.getLong(sourcePointer + index + 8))
            memory.putLong(destinationPointer + index + 16, source.getLong(sourcePointer + index + 16))
            memory.putLong(destinationPointer + index + 24, source.getLong(sourcePointer + index + 24))
            index += UNROLLED_BYTES
        }
        while (index <= bytesToCopy - Long.SIZE_BYTES) {
            memory.putLong(destinationPointer + index, source.getLong(sourcePointer + index))
            index += Long.SIZE_BYTES
        }
        while (index < bytesToCopy) {
            memory.put(destinationPointer + index, source.get(sourcePointer + index))
            index++
        }
    }

    private fun copyBackward(sourcePointer: Int, destinationPointer: Int, bytesToCopy: Int) {
        var remaining = bytesToCopy
        while (remaining >= UNROLLED_BYTES) {
            remaining -= UNROLLED_BYTES
            memory.putLong(destinationPointer + remaining + 24, memory.getLong(sourcePointer + remaining + 24))
            memory.putLong(destinationPointer + remaining + 16, memory.getLong(sourcePointer + remaining + 16))
            memory.putLong(destinationPointer + remaining + 8, memory.getLong(sourcePointer + remaining + 8))
            memory.putLong(destinationPointer + remaining, memory.getLong(sourcePointer + remaining))
        }
        while (remaining >= Long.SIZE_BYTES) {
            remaining -= Long.SIZE_BYTES
            memory.putLong(destinationPointer + remaining, memory.getLong(sourcePointer + remaining))
        }
        while (remaining > 0) {
            remaining--
            memory.put(destinationPointer + remaining, memory.get(sourcePointer + remaining))
        }
    }

    private fun checkRange(pointer: Int, byteCount: Int, size: Int) {
        if (pointer < 0 || byteCount < 0 || pointer > size - byteCount) {
            throw IndexOutOfBoundsException()
        }
    }

    private companion object {
        const val BULK_COPY_THRESHOLD = 64
        const val REPEATED_BYTE_MASK = 0x0101010101010101L
        const val UNROLLED_BYTES = 4 * Long.SIZE_BYTES
    }
}
