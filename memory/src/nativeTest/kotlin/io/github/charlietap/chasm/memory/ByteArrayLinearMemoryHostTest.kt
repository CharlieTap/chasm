package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@OptIn(UnsafeHostApi::class)
class ByteArrayLinearMemoryHostTest {

    @Test
    fun `grows the backing array while preserving the memory object and contents`() {
        val originalArray = ByteArray(PAGE_SIZE)
        val memory = ByteArrayLinearMemory(originalArray)
        memory.writeI8(PAGE_SIZE - 1, 47)

        assertSame(memory, memory.grow(1))
        assertNotSame(originalArray, memory.memory)
        assertEquals(PAGE_SIZE * 2, memory.byteSize)
        assertEquals(47.toByte(), memory.readI8(PAGE_SIZE - 1))
    }

    @Test
    fun `reads and writes scalar and reusable buffer values`() {
        val backing = ByteArray(64)
        val memory = ByteArrayLinearMemory(backing)
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)
        val source = byteArrayOf(10, 11, 12, 13, 14, 15)
        val destination = ByteArray(8) { 99 }

        memory.writeI16(0, 0x1234)
        memory.writeI32(2, 0x12345678)
        memory.writeI64(6, 0x123456789ABCDEFL)
        memory.writeF32(14, float)
        memory.writeF64(18, double)
        memory.write(30, source, bufferPointer = 1, bytesToWrite = 4)
        val returned = memory.read(destination, memoryPointer = 30, bytesToRead = 4, bufferPointer = 2)

        assertEquals(0x1234.toShort(), memory.readI16(0))
        assertEquals(0x12345678, memory.readI32(2))
        assertEquals(0x123456789ABCDEFL, memory.readI64(6))
        assertEquals(float.toRawBits(), memory.readF32(14).toRawBits())
        assertEquals(double.toRawBits(), memory.readF64(18).toRawBits())
        assertSame(destination, returned)
        assertContentEquals(byteArrayOf(99, 99, 11, 12, 13, 14, 99, 99), destination)
        assertSame(backing, memory.unsafeBorrowByteArray())
    }

    @Test
    fun `fills copies and moves overlapping and cross memory ranges`() {
        val memory = ByteArrayLinearMemory(ByteArray(16))
        val destination = ByteArrayLinearMemory(ByteArray(16))
        memory.write(0, ByteArray(10) { it.toByte() })

        memory.move(sourcePointer = 0, destinationPointer = 2, bytesToMove = 6)
        assertContentEquals(
            byteArrayOf(0, 1, 0, 1, 2, 3, 4, 5, 8, 9),
            memory.read(ByteArray(10), 0, 10),
        )

        destination.fill(memoryPointer = 0, value = 7, bytesToFill = 10)
        destination.copy(sourcePointer = 2, destinationPointer = 3, bytesToCopy = 6, source = memory)
        assertContentEquals(
            byteArrayOf(7, 7, 7, 0, 1, 2, 3, 4, 5, 7),
            destination.read(ByteArray(10), 0, 10),
        )
    }
}
