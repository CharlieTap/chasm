package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomicCompareExchangeJvmTest {

    @Test
    fun i32CompareExchangeSucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0x1234)

        val result = I32AtomicCompareExchange(memory, address, 0x1234, 0x5678)

        assertEquals(0x1234, result)
        assertEquals(0x5678, buffer.getInt(address))
    }

    @Test
    fun i32CompareExchangeFailsWhenExpectedMismatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAAAA)

        val result = I32AtomicCompareExchange(memory, address, 0x1234, 0x5678)

        assertEquals(0xAAAA, result)
        assertEquals(0xAAAA, buffer.getInt(address))
    }

    @Test
    fun i32CompareExchange8SucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I32AtomicCompareExchange8(memory, address + 1, 0xCC, 0x11)

        assertEquals(0xCC, result)
        assertEquals(0xAABB11DD.toInt(), buffer.getInt(address))
    }

    @Test
    fun i32CompareExchange8FailsWhenExpectedMismatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I32AtomicCompareExchange8(memory, address + 1, 0xFF, 0x11)

        assertEquals(0xCC, result)
        assertEquals(0xAABBCCDD.toInt(), buffer.getInt(address))
    }

    @Test
    fun i32CompareExchange16SucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I32AtomicCompareExchange16(memory, address, 0xCCDD, 0x1122)

        assertEquals(0xCCDD, result)
        assertEquals(0xAABB1122.toInt(), buffer.getInt(address))
    }

    @Test
    fun i64CompareExchangeSucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        val initial = 0x1234567890ABCDEFL
        val replacement = 0x0FEDCBA987654321L

        buffer.putLong(address, initial)

        val result = I64AtomicCompareExchange(memory, address, initial, replacement)

        assertEquals(initial, result)
        assertEquals(replacement, buffer.getLong(address))
    }

    @Test
    fun i64CompareExchange8SucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0x11223344)

        val result = I64AtomicCompareExchange8(memory, address + 2, 0x22L, 0xAAL)

        assertEquals(0x22L, result)
        assertEquals(0x11AA3344, buffer.getInt(address))
    }

    @Test
    fun i64CompareExchange16SucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I64AtomicCompareExchange16(memory, address, 0xCCDDL, 0x1122L)

        assertEquals(0xCCDDL, result)
        assertEquals(0xAABB1122.toInt(), buffer.getInt(address))
    }

    @Test
    fun i64CompareExchange32SucceedsWhenExpectedMatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I64AtomicCompareExchange32(memory, address, 0xAABBCCDDL, 0x11223344L)

        assertEquals(0xAABBCCDDL, result)
        assertEquals(0x11223344, buffer.getInt(address))
    }

    @Test
    fun i64CompareExchange32FailsWhenExpectedMismatches() {
        val memory = linearMemory()
        val buffer = memory.memory
        val address = 0
        buffer.putInt(address, 0xAABBCCDD.toInt())

        val result = I64AtomicCompareExchange32(memory, address, 0x12345678L, 0x11223344L)

        assertEquals(0xAABBCCDDL, result)
        assertEquals(0xAABBCCDD.toInt(), buffer.getInt(address))
    }

    private fun linearMemory() = ByteBufferLinearMemory(LinearMemory.Pages(1u))
}

