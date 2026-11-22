package io.github.charlietap.chasm.embedding.memory

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class MemoryCallsTest {

    @Test
    fun `can size a memory instance`() {

        val memory = onePageMemory()

        assertEquals(1, sizeMemory(memory))
        assertEquals(PAGE_SIZE, memoryByteSize(memory))
    }

    @Test
    fun `can read a byte from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, byteArrayOf(117))

        assertEquals(117.toByte(), readByte(memory, 118))
    }

    @Test
    fun `can read a short from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, byteArrayOf(0x75, 0x01))

        assertEquals(0x0175.toShort(), readShort(memory, 118))
    }

    @Test
    fun `can read an int from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, byteArrayOf(0x75, 0x00, 0x00, 0x00))

        assertEquals(117, readInt(memory, 118))
    }

    @Test
    fun `can read a long from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, byteArrayOf(0x75, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01))

        assertEquals(0x0100000000000075L, readLong(memory, 118))
    }

    @Test
    fun `can read a float from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, littleEndian(117.5f.toRawBits().toLong(), 4))

        assertEquals(117.5f, readFloat(memory, 118))
    }

    @Test
    fun `can read a double from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, littleEndian(117.5.toRawBits(), 8))

        assertEquals(117.5, readDouble(memory, 118))
    }

    @Test
    fun `can read bytes from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 118, byteArrayOf(117, 118))
        val buffer = ByteArray(120)

        val actual = readBytes(memory, buffer, 118, 2, 117)

        assertSame(buffer, actual)
        assertContentEquals(byteArrayOf(117, 118), buffer.copyOfRange(117, 119))
    }

    @Test
    fun `can read a utf8 encoded string from a memory instance`() {

        val memory = onePageMemory()
        val bytes = "Hello world".encodeToByteArray()
        writeBytes(memory, 1, bytes)

        assertEquals("Hello world", readUtf8String(memory, 1, bytes.size))
    }

    @Test
    fun `can read a null terminated utf8 encoded string from a memory instance`() {

        val memory = onePageMemory()
        writeBytes(memory, 1, "Hello world".encodeToByteArray() + byteArrayOf(0))

        assertEquals("Hello world", readNullTerminatedUtf8String(memory, 1))
    }

    @Test
    fun `can write a byte to a memory instance`() {

        val memory = onePageMemory()

        writeByte(memory, 118, 117)

        assertContentEquals(byteArrayOf(117), bytes(memory, 118, 1))
    }

    @Test
    fun `can write a short to a memory instance`() {

        val memory = onePageMemory()

        writeShort(memory, 118, 0x0175)

        assertContentEquals(byteArrayOf(0x75, 0x01), bytes(memory, 118, 2))
    }

    @Test
    fun `can write an int to a memory instance`() {

        val memory = onePageMemory()

        writeInt(memory, 118, 117)

        assertContentEquals(byteArrayOf(0x75, 0x00, 0x00, 0x00), bytes(memory, 118, 4))
    }

    @Test
    fun `can write a long to a memory instance`() {

        val memory = onePageMemory()

        writeLong(memory, 118, 0x0100000000000075L)

        assertContentEquals(byteArrayOf(0x75, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01), bytes(memory, 118, 8))
    }

    @Test
    fun `can write a float to a memory instance`() {

        val memory = onePageMemory()

        writeFloat(memory, 118, 117.5f)

        assertContentEquals(littleEndian(117.5f.toRawBits().toLong(), 4), bytes(memory, 118, 4))
    }

    @Test
    fun `can write a double to a memory instance`() {

        val memory = onePageMemory()

        writeDouble(memory, 118, 117.5)

        assertContentEquals(littleEndian(117.5.toRawBits(), 8), bytes(memory, 118, 8))
    }

    @Test
    fun `can write a range from a buffer to a memory instance`() {

        val memory = onePageMemory()
        val buffer = byteArrayOf(116, 117, 118, 119)

        writeBytes(memory, 118, buffer, bufferPointer = 1, bytesToWrite = 2)

        assertContentEquals(byteArrayOf(0, 117, 118, 0), bytes(memory, 117, 4))
    }

    @Test
    fun `can write bytes to a memory instance`() {

        val memory = onePageMemory()

        writeBytes(memory, 118, byteArrayOf(117, 118))

        assertContentEquals(byteArrayOf(117, 118), bytes(memory, 118, 2))
    }

    @Test
    fun `can write a utf8 encoded string to a memory instance`() {

        val memory = onePageMemory()

        writeUtf8String(memory, 1, "Hello world")

        assertContentEquals("Hello world".encodeToByteArray(), bytes(memory, 1, 11))
    }

    @Test
    fun `can fill copy and move memory`() {

        val memory = onePageMemory()

        fillMemory(memory, 0, 7, 4)
        copyMemory(memory, 0, memory, 8, 2)
        moveMemory(memory, 8, memory, 9, 2)

        assertContentEquals(byteArrayOf(7, 7, 7, 7, 0, 0, 0, 0, 7, 7, 7), bytes(memory, 0, 11))
    }

    @Test
    fun `accesses outside the memory throw index out of bounds`() {

        val memory = onePageMemory()

        assertFailsWith<IndexOutOfBoundsException> { readInt(memory, PAGE_SIZE - 2) }
        assertFailsWith<IndexOutOfBoundsException> { writeInt(memory, PAGE_SIZE - 2, 1) }
        assertFailsWith<IndexOutOfBoundsException> { readBytes(memory, ByteArray(4), PAGE_SIZE - 2, 4, 0) }
        assertFailsWith<IndexOutOfBoundsException> { writeBytes(memory, PAGE_SIZE - 2, ByteArray(4)) }
        assertFailsWith<IndexOutOfBoundsException> { writeUtf8String(memory, PAGE_SIZE - 1, "hi") }
        assertFailsWith<IndexOutOfBoundsException> { fillMemory(memory, PAGE_SIZE - 1, 0, 2) }
    }

    private fun onePageMemory(): Memory {
        return memory(interpreterStore(), memoryType(limits = limits(min = 1u)))
    }

    private fun bytes(
        memory: Memory,
        pointer: Int,
        count: Int,
    ): ByteArray {
        return readBytes(memory, ByteArray(count), pointer, count, 0)
    }

    private fun littleEndian(
        bits: Long,
        width: Int,
    ): ByteArray {
        return ByteArray(width) { index -> (bits shr (index * 8)).toByte() }
    }
}
