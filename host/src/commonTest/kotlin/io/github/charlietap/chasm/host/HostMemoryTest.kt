package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HostMemoryTest {

    @Test
    fun `reads a fixed length utf8 string`() {
        val encoded = "héllo".encodeToByteArray()
        val memory = TestHostMemory(
            ByteArray(16).apply {
                encoded.copyInto(this, destinationOffset = 3)
            },
        )

        assertEquals("héllo", memory.readUtf8String(3, encoded.size))
    }

    @Test
    fun `reads a null terminated utf8 string`() {
        val encoded = "héllo".encodeToByteArray()
        val memory = TestHostMemory(
            ByteArray(16) { 1 }.apply {
                encoded.copyInto(this, destinationOffset = 3)
                this[3 + encoded.size] = 0
            },
        )

        assertEquals("héllo", memory.readNullTerminatedUtf8String(3))
    }

    @Test
    fun `scans null terminated strings a word at a time`() {
        val memory = TestHostMemory(
            ByteArray(64) { 1 }.apply {
                this[57] = 0
            },
        )

        memory.readNullTerminatedUtf8String(3)

        assertEquals(7, memory.i64Reads)
        assertEquals(0, memory.i8Reads)
    }

    @Test
    fun `fails a null terminated read when memory contains no terminator`() {
        val memory = TestHostMemory(ByteArray(8) { 1 })

        assertFailsWith<IndexOutOfBoundsException> {
            memory.readNullTerminatedUtf8String(3)
        }
    }

    @Test
    fun `writes a utf8 string without a terminator`() {
        val backing = ByteArray(16)
        val memory = TestHostMemory(backing)

        memory.writeUtf8String(3, "héllo")

        assertContentEquals(
            byteArrayOf(0, 0, 0) + "héllo".encodeToByteArray() + ByteArray(7),
            backing,
        )
    }
}

private class TestHostMemory(
    private val memory: ByteArray,
) : HostMemory {

    var i8Reads = 0
    var i64Reads = 0

    override val byteSize: Int
        get() = memory.size

    override fun readI8(memoryPointer: Int): Byte {
        i8Reads++
        return memory[memoryPointer]
    }

    override fun readI16(memoryPointer: Int): Short = error("unused")

    override fun readI32(memoryPointer: Int): Int = error("unused")

    override fun readI64(memoryPointer: Int): Long {
        i64Reads++
        var value = 0L
        repeat(Long.SIZE_BYTES) { byteOffset ->
            value = value or (
                (memory[memoryPointer + byteOffset].toLong() and 0xFFL) shl
                    (byteOffset * Byte.SIZE_BITS)
            )
        }
        return value
    }

    override fun readF32(memoryPointer: Int): Float = error("unused")

    override fun readF64(memoryPointer: Int): Double = error("unused")

    override fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        memory.copyInto(
            destination = buffer,
            destinationOffset = bufferPointer,
            startIndex = memoryPointer,
            endIndex = memoryPointer + bytesToRead,
        )
        return buffer
    }

    override fun writeI8(memoryPointer: Int, value: Byte) = error("unused")

    override fun writeI16(memoryPointer: Int, value: Short) = error("unused")

    override fun writeI32(memoryPointer: Int, value: Int) = error("unused")

    override fun writeI64(memoryPointer: Int, value: Long) = error("unused")

    override fun writeF32(memoryPointer: Int, value: Float) = error("unused")

    override fun writeF64(memoryPointer: Int, value: Double) = error("unused")

    override fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        buffer.copyInto(
            destination = memory,
            destinationOffset = memoryPointer,
            startIndex = bufferPointer,
            endIndex = bufferPointer + bytesToWrite,
        )
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) = error("unused")

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) = error("unused")

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) = error("unused")
}
