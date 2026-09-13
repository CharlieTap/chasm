package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.host.ByteBufferHostMemory
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_WRITE
import java.nio.file.Files
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.DELETE_ON_CLOSE
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.SPARSE
import java.nio.file.StandardOpenOption.WRITE

internal const val MAX_JVM_MEMORY_PAGES = Int.MAX_VALUE / LinearMemory.PAGE_SIZE
internal const val MAX_JVM_MEMORY_BYTES = MAX_JVM_MEMORY_PAGES * LinearMemory.PAGE_SIZE

@OptIn(UnsafeHostApi::class)
class ByteBufferLinearMemory private constructor(
    state: ByteBufferState,
) : LinearMemory, ByteBufferHostMemory {

    internal var mapping: ByteBuffer = state.mapping
        private set

    private var maximumByteSize: Long = state.maximumByteSize
    private var prefault: Boolean = state.prefault
    private var logicalMemory: ByteBuffer = state.memory
    private var copySource: ByteBuffer = duplicate(state.memory)

    @PublishedApi
    internal val memory: ByteBuffer
        get() = logicalMemory

    constructor(
        pages: LinearMemory.Pages,
        maximumPages: LinearMemory.Pages? = null,
        config: LinearMemoryConfig = LinearMemoryConfig(),
    ) : this(createMappedByteBufferState(pages, maximumPages, config))

    override val byteSize: Int
        get() = logicalMemory.limit()

    override fun grow(pagesToAdd: Int): LinearMemory {
        require(pagesToAdd >= 0) { "Linear memory cannot shrink" }
        if (pagesToAdd == 0) return this

        val previousByteSize = byteSize
        val nextByteSize = previousByteSize.toLong() + pagesToAdd.toLong() * LinearMemory.PAGE_SIZE
        require(nextByteSize <= maximumByteSize) {
            "JVM linear memory cannot exceed ${maximumByteSize / LinearMemory.PAGE_SIZE} pages"
        }

        if (prefault) {
            loadMappedRange(mapping, previousByteSize, (nextByteSize - previousByteSize).toInt())
        }
        replaceLogicalMemory(bufferForSize(mapping, nextByteSize.toInt()))
        return this
    }

    override fun readI8(memoryPointer: Int): Byte = logicalMemory.get(memoryPointer)

    override fun readI16(memoryPointer: Int): Short = logicalMemory.getShort(memoryPointer)

    override fun readI32(memoryPointer: Int): Int = logicalMemory.getInt(memoryPointer)

    override fun readI64(memoryPointer: Int): Long = logicalMemory.getLong(memoryPointer)

    override fun readF32(memoryPointer: Int): Float = logicalMemory.getFloat(memoryPointer)

    override fun readF64(memoryPointer: Int): Double = logicalMemory.getDouble(memoryPointer)

    override fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        checkRange(memoryPointer, bytesToRead, byteSize)
        checkRange(bufferPointer, bytesToRead, buffer.size)
        logicalMemory.get(memoryPointer, buffer, bufferPointer, bytesToRead)
        return buffer
    }

    override fun writeI8(memoryPointer: Int, value: Byte) {
        logicalMemory.put(memoryPointer, value)
    }

    override fun writeI16(memoryPointer: Int, value: Short) {
        logicalMemory.putShort(memoryPointer, value)
    }

    override fun writeI32(memoryPointer: Int, value: Int) {
        logicalMemory.putInt(memoryPointer, value)
    }

    override fun writeI64(memoryPointer: Int, value: Long) {
        logicalMemory.putLong(memoryPointer, value)
    }

    override fun writeF32(memoryPointer: Int, value: Float) {
        logicalMemory.putFloat(memoryPointer, value)
    }

    override fun writeF64(memoryPointer: Int, value: Double) {
        logicalMemory.putDouble(memoryPointer, value)
    }

    override fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        checkRange(memoryPointer, bytesToWrite, byteSize)
        checkRange(bufferPointer, bytesToWrite, buffer.size)
        logicalMemory.put(memoryPointer, buffer, bufferPointer, bytesToWrite)
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) {
        checkRange(memoryPointer, bytesToFill, byteSize)
        val repeatedValue = (value.toLong() and 0xFFL) * REPEATED_BYTE_MASK
        var index = 0
        while (index <= bytesToFill - UNROLLED_BYTES) {
            logicalMemory.putLong(memoryPointer + index, repeatedValue)
            logicalMemory.putLong(memoryPointer + index + 8, repeatedValue)
            logicalMemory.putLong(memoryPointer + index + 16, repeatedValue)
            logicalMemory.putLong(memoryPointer + index + 24, repeatedValue)
            index += UNROLLED_BYTES
        }
        while (index <= bytesToFill - Long.SIZE_BYTES) {
            logicalMemory.putLong(memoryPointer + index, repeatedValue)
            index += Long.SIZE_BYTES
        }
        while (index < bytesToFill) {
            logicalMemory.put(memoryPointer + index, value)
            index++
        }
    }

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) {
        val sourceBuffer = if (source === this) {
            copySource
        } else {
            (source as ByteBufferHostMemory).unsafeBorrowByteBuffer()
        }
        checkRange(sourcePointer, bytesToCopy, sourceBuffer.limit())
        checkRange(destinationPointer, bytesToCopy, byteSize)
        copyForward(sourceBuffer, sourcePointer, destinationPointer, bytesToCopy)
    }

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) {
        val sourceBuffer = if (source === this) {
            copySource
        } else {
            (source as ByteBufferHostMemory).unsafeBorrowByteBuffer()
        }
        checkRange(sourcePointer, bytesToMove, sourceBuffer.limit())
        checkRange(destinationPointer, bytesToMove, byteSize)

        if (
            source !== this ||
            destinationPointer >= sourcePointer + bytesToMove ||
            sourcePointer >= destinationPointer + bytesToMove
        ) {
            copyForward(sourceBuffer, sourcePointer, destinationPointer, bytesToMove)
        } else if (destinationPointer > sourcePointer) {
            copyBackward(sourcePointer, destinationPointer, bytesToMove)
        } else if (destinationPointer < sourcePointer) {
            copyForwardUnrolled(sourceBuffer, sourcePointer, destinationPointer, bytesToMove)
        }
    }

    @UnsafeHostApi
    override fun unsafeBorrowByteBuffer(): ByteBuffer = duplicate(logicalMemory)

    internal fun release() {
        val empty = ByteBuffer.allocateDirect(0).order(ByteOrder.LITTLE_ENDIAN)
        mapping = empty
        maximumByteSize = 0
        prefault = false
        replaceLogicalMemory(empty)
    }

    private fun replaceLogicalMemory(value: ByteBuffer) {
        logicalMemory = value
        copySource = duplicate(value)
    }

    private fun copyForward(
        source: ByteBuffer,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
    ) {
        if (bytesToCopy < BULK_COPY_THRESHOLD) {
            copyForwardUnrolled(source, sourcePointer, destinationPointer, bytesToCopy)
        } else {
            logicalMemory.put(destinationPointer, source, sourcePointer, bytesToCopy)
        }
    }

    private fun copyForwardUnrolled(
        source: ByteBuffer,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
    ) {
        var index = 0
        while (index <= bytesToCopy - UNROLLED_BYTES) {
            logicalMemory.putLong(destinationPointer + index, source.getLong(sourcePointer + index))
            logicalMemory.putLong(destinationPointer + index + 8, source.getLong(sourcePointer + index + 8))
            logicalMemory.putLong(destinationPointer + index + 16, source.getLong(sourcePointer + index + 16))
            logicalMemory.putLong(destinationPointer + index + 24, source.getLong(sourcePointer + index + 24))
            index += UNROLLED_BYTES
        }
        while (index <= bytesToCopy - Long.SIZE_BYTES) {
            logicalMemory.putLong(destinationPointer + index, source.getLong(sourcePointer + index))
            index += Long.SIZE_BYTES
        }
        while (index < bytesToCopy) {
            logicalMemory.put(destinationPointer + index, source.get(sourcePointer + index))
            index++
        }
    }

    private fun copyBackward(sourcePointer: Int, destinationPointer: Int, bytesToCopy: Int) {
        var remaining = bytesToCopy
        while (remaining >= UNROLLED_BYTES) {
            remaining -= UNROLLED_BYTES
            logicalMemory.putLong(
                destinationPointer + remaining + 24,
                logicalMemory.getLong(sourcePointer + remaining + 24),
            )
            logicalMemory.putLong(
                destinationPointer + remaining + 16,
                logicalMemory.getLong(sourcePointer + remaining + 16),
            )
            logicalMemory.putLong(
                destinationPointer + remaining + 8,
                logicalMemory.getLong(sourcePointer + remaining + 8),
            )
            logicalMemory.putLong(destinationPointer + remaining, logicalMemory.getLong(sourcePointer + remaining))
        }
        while (remaining >= Long.SIZE_BYTES) {
            remaining -= Long.SIZE_BYTES
            logicalMemory.putLong(destinationPointer + remaining, logicalMemory.getLong(sourcePointer + remaining))
        }
        while (remaining > 0) {
            remaining--
            logicalMemory.put(destinationPointer + remaining, logicalMemory.get(sourcePointer + remaining))
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

        fun duplicate(buffer: ByteBuffer): ByteBuffer = buffer.duplicate().order(buffer.order())
    }
}

private class ByteBufferState(
    val mapping: ByteBuffer,
    val memory: ByteBuffer,
    val maximumByteSize: Long,
    val prefault: Boolean,
)

private fun createMappedByteBufferState(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
): ByteBufferState {
    val initialPages = pages.amount.toLong()
    require(initialPages <= MAX_JVM_MEMORY_PAGES) {
        "JVM linear memory cannot exceed $MAX_JVM_MEMORY_PAGES pages"
    }
    val declaredMaximumPages = maximumPages?.amount?.toLong()
    require(declaredMaximumPages == null || declaredMaximumPages >= initialPages) {
        "Linear memory maximum cannot be smaller than its initial size"
    }

    val maximumPageCount = minOf(declaredMaximumPages ?: MAX_JVM_MEMORY_PAGES.toLong(), MAX_JVM_MEMORY_PAGES.toLong())
    val initialByteSize = (initialPages * LinearMemory.PAGE_SIZE).toInt()
    val maximumByteSize = (maximumPageCount * LinearMemory.PAGE_SIZE).toInt()

    // SPARSE is only considered while creating a file. Reserve a unique name
    // with the default provider, then recreate that path through FileChannel.
    val backingFile = Files.createTempFile("chasm-memory-", ".bin")
    Files.delete(backingFile)
    val channel = FileChannel.open(backingFile, READ, WRITE, CREATE_NEW, SPARSE, DELETE_ON_CLOSE)

    try {
        val mapping = mapBackingFile(channel, maximumByteSize)
        channel.close()
        if (config.prefault) loadMappedRange(mapping, 0, initialByteSize)
        return ByteBufferState(
            mapping = mapping,
            memory = bufferForSize(mapping, initialByteSize),
            maximumByteSize = maximumByteSize.toLong(),
            prefault = config.prefault,
        )
    } catch (error: Throwable) {
        try {
            channel.close()
        } catch (closeError: Throwable) {
            error.addSuppressed(closeError)
        }
        try {
            Files.deleteIfExists(backingFile)
        } catch (deleteError: Throwable) {
            error.addSuppressed(deleteError)
        }
        throw error
    }
}

private fun mapBackingFile(
    channel: FileChannel,
    byteSize: Int,
): ByteBuffer = if (byteSize == 0) {
    ByteBuffer.allocateDirect(0).order(ByteOrder.LITTLE_ENDIAN)
} else {
    channel.map(READ_WRITE, 0, byteSize.toLong()).order(ByteOrder.LITTLE_ENDIAN)
}

private fun bufferForSize(
    mapping: ByteBuffer,
    byteSize: Int,
): ByteBuffer = mapping
    .slice(0, byteSize)
    .order(ByteOrder.LITTLE_ENDIAN)

private fun loadMappedRange(mapping: ByteBuffer, offset: Int, byteCount: Int) {
    if (byteCount > 0 && mapping is MappedByteBuffer) {
        mapping.slice(offset, byteCount).load()
    }
}
