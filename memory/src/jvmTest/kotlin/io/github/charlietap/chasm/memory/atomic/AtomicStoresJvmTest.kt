package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteOrder

class AtomicStoresJvmTest {

    @Test
    fun i32StoreWritesValue() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 0
        val value = 0x11223344

        I32AtomicStore(memory, address, value)

        assertEquals(value, buffer.getInt(address))
    }

    @Test
    fun i32Store8UpdatesTargetByteOnly() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 0
        buffer.putInt(address, 0xA1B2C3D4.toInt())

        I32AtomicStore8(memory, address + 2, 0x15C)

        assertEquals(0xD4.toByte(), buffer.get(address))
        assertEquals(0xC3.toByte(), buffer.get(address + 1))
        assertEquals(0x5C.toByte(), buffer.get(address + 2))
        assertEquals(0xA1.toByte(), buffer.get(address + 3))
    }

    @Test
    fun i32Store16WritesHalfword() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 4
        buffer.putInt(address, 0)

        I32AtomicStore16(memory, address, 0xBEEF)

        assertEquals(0xEF.toByte(), buffer.get(address))
        assertEquals(0xBE.toByte(), buffer.get(address + 1))
        assertEquals(0x00.toByte(), buffer.get(address + 2))
        assertEquals(0x00.toByte(), buffer.get(address + 3))
    }

    @Test
    fun i64StoreWritesValue() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 16
        val value = 0x0123456789ABCDEFL

        I64AtomicStore(memory, address, value)

        assertEquals(value, buffer.getLong(address))
    }

    @Test
    fun i64Store8WritesSingleByte() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 5
        buffer.putLong(0, 0L)

        I64AtomicStore8(memory, address, 0x1AA)

        assertEquals(0xAA.toByte(), buffer.get(address))
        assertEquals(0x00.toByte(), buffer.get(address - 1))
        assertEquals(0x00.toByte(), buffer.get(address + 1))
    }

    @Test
    fun i64Store16WritesHalfword() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 32
        buffer.putLong(address, 0L)

        I64AtomicStore16(memory, address, 0xFACE)

        assertEquals(0xCE.toByte(), buffer.get(address))
        assertEquals(0xFA.toByte(), buffer.get(address + 1))
        assertEquals(0x00.toByte(), buffer.get(address + 2))
        assertEquals(0x00.toByte(), buffer.get(address + 3))
    }

    @Test
    fun i64Store32WritesLowerBitsOnly() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val address = 24
        buffer.putLong(address, -1L)

        val value = 0x123456789ABCDEF0L
        I64AtomicStore32(memory, address, value)

        assertEquals(0xF0.toByte(), buffer.get(address))
        assertEquals(0xDE.toByte(), buffer.get(address + 1))
        assertEquals(0xBC.toByte(), buffer.get(address + 2))
        assertEquals(0x9A.toByte(), buffer.get(address + 3))
        assertEquals(0xFF.toByte(), buffer.get(address + 4))
    }

    private fun linearMemory() = ByteBufferLinearMemory(LinearMemory.Pages(1u))
}

