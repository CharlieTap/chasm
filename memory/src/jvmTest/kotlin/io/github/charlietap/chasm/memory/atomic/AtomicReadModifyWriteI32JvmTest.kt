package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomicReadModifyWriteI32JvmTest {

    @Test
    fun i32RmwAddReturnsOldValueAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 100)

        val result = I32AtomicRmwAdd(memory, 0, 50)

        assertEquals(100, result)
        assertEquals(150, buffer.getInt(0))
    }

    @Test
    fun i32RmwSubReturnsOldValueAndSubtracts() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 100)

        val result = I32AtomicRmwSub(memory, 0, 30)

        assertEquals(100, result)
        assertEquals(70, buffer.getInt(0))
    }

    @Test
    fun i32RmwAndReturnsOldValueAndAnds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0b1111_0000)

        val result = I32AtomicRmwAnd(memory, 0, 0b1010_1010)

        assertEquals(0b1111_0000, result)
        assertEquals(0b1010_0000, buffer.getInt(0))
    }

    @Test
    fun i32RmwOrReturnsOldValueAndOrs() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0b1111_0000)

        val result = I32AtomicRmwOr(memory, 0, 0b0000_1111)

        assertEquals(0b1111_0000, result)
        assertEquals(0b1111_1111, buffer.getInt(0))
    }

    @Test
    fun i32RmwXorReturnsOldValueAndXors() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0b1111_0000)

        val result = I32AtomicRmwXor(memory, 0, 0b1010_1010)

        assertEquals(0b1111_0000, result)
        assertEquals(0b0101_1010, buffer.getInt(0))
    }

    @Test
    fun i32RmwExchangeReturnsOldValueAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x12345678)

        val result = I32AtomicRmwExchange(memory, 0, 0xABCDEF00.toInt())

        assertEquals(0x12345678, result)
        assertEquals(0xABCDEF00.toInt(), buffer.getInt(0))
    }

    @Test
    fun i32Rmw8AddReturnsOldByteAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I32AtomicRmw8Add(memory, 1, 0x10)

        assertEquals(0x33, result)
        assertEquals(0x11224344, buffer.getInt(0))
    }

    @Test
    fun i32Rmw8SubReturnsOldByteAndSubtracts() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I32AtomicRmw8Sub(memory, 0, 0x04)

        assertEquals(0x44, result)
        assertEquals(0x11223340, buffer.getInt(0))
    }

    @Test
    fun i32Rmw8AndReturnsOldByteAndAnds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x112233FF.toInt())

        val result = I32AtomicRmw8And(memory, 0, 0x0F)

        assertEquals(0xFF, result)
        assertEquals(0x1122330F, buffer.getInt(0))
    }

    @Test
    fun i32Rmw8ExchangeReturnsOldByteAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I32AtomicRmw8Exchange(memory, 2, 0xAB)

        assertEquals(0x22, result)
        assertEquals(0x11AB3344, buffer.getInt(0))
    }

    @Test
    fun i32Rmw16AddReturnsOldHalfwordAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11220100)

        val result = I32AtomicRmw16Add(memory, 0, 0x0050)

        assertEquals(0x0100, result)
        assertEquals(0x11220150, buffer.getInt(0))
    }

    @Test
    fun i32Rmw16SubReturnsOldHalfwordAndSubtracts() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11221000)

        val result = I32AtomicRmw16Sub(memory, 0, 0x0100)

        assertEquals(0x1000, result)
        assertEquals(0x11220F00, buffer.getInt(0))
    }

    @Test
    fun i32Rmw16ExchangeReturnsOldHalfwordAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I32AtomicRmw16Exchange(memory, 0, 0xABCD)

        assertEquals(0x3344, result)
        assertEquals(0x1122ABCD, buffer.getInt(0))
    }

    private fun linearMemory() = ByteBufferLinearMemory(LinearMemory.Pages(1u))
}

