package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomicReadModifyWriteI64JvmTest {

    @Test
    fun i64RmwAddReturnsOldValueAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 100L)

        val result = I64AtomicRmwAdd(memory, 0, 50L)

        assertEquals(100L, result)
        assertEquals(150L, buffer.getLong(0))
    }

    @Test
    fun i64RmwSubReturnsOldValueAndSubtracts() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 100L)

        val result = I64AtomicRmwSub(memory, 0, 30L)

        assertEquals(100L, result)
        assertEquals(70L, buffer.getLong(0))
    }

    @Test
    fun i64RmwAndReturnsOldValueAndAnds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0b1111_0000L)

        val result = I64AtomicRmwAnd(memory, 0, 0b1010_1010L)

        assertEquals(0b1111_0000L, result)
        assertEquals(0b1010_0000L, buffer.getLong(0))
    }

    @Test
    fun i64RmwOrReturnsOldValueAndOrs() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0b1111_0000L)

        val result = I64AtomicRmwOr(memory, 0, 0b0000_1111L)

        assertEquals(0b1111_0000L, result)
        assertEquals(0b1111_1111L, buffer.getLong(0))
    }

    @Test
    fun i64RmwXorReturnsOldValueAndXors() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0b1111_0000L)

        val result = I64AtomicRmwXor(memory, 0, 0b1010_1010L)

        assertEquals(0b1111_0000L, result)
        assertEquals(0b0101_1010L, buffer.getLong(0))
    }

    @Test
    fun i64RmwExchangeReturnsOldValueAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0x1234567890ABCDEFL)

        val result = I64AtomicRmwExchange(memory, 0, 0x0FEDCBA987654321L)

        assertEquals(0x1234567890ABCDEFL, result)
        assertEquals(0x0FEDCBA987654321L, buffer.getLong(0))
    }

    @Test
    fun i64Rmw8AddReturnsOldByteAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I64AtomicRmw8Add(memory, 1, 0x10L)

        assertEquals(0x33L, result)
        assertEquals(0x11224344, buffer.getInt(0))
    }

    @Test
    fun i64Rmw8ExchangeReturnsOldByteAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I64AtomicRmw8Exchange(memory, 2, 0xABL)

        assertEquals(0x22L, result)
        assertEquals(0x11AB3344, buffer.getInt(0))
    }

    @Test
    fun i64Rmw16AddReturnsOldHalfwordAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11220100)

        val result = I64AtomicRmw16Add(memory, 0, 0x0050L)

        assertEquals(0x0100L, result)
        assertEquals(0x11220150, buffer.getInt(0))
    }

    @Test
    fun i64Rmw16ExchangeReturnsOldHalfwordAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x11223344)

        val result = I64AtomicRmw16Exchange(memory, 0, 0xABCDL)

        assertEquals(0x3344L, result)
        assertEquals(0x1122ABCD, buffer.getInt(0))
    }

    @Test
    fun i64Rmw32AddReturnsOldWordAndAdds() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 100)

        val result = I64AtomicRmw32Add(memory, 0, 50L)

        assertEquals(100L, result)
        assertEquals(150, buffer.getInt(0))
    }

    @Test
    fun i64Rmw32SubReturnsOldWordAndSubtracts() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 100)

        val result = I64AtomicRmw32Sub(memory, 0, 30L)

        assertEquals(100L, result)
        assertEquals(70, buffer.getInt(0))
    }

    @Test
    fun i64Rmw32ExchangeReturnsOldWordAndSetsNew() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x12345678)

        val result = I64AtomicRmw32Exchange(memory, 0, 0xABCDEF00L)

        assertEquals(0x12345678L, result)
        assertEquals(0xABCDEF00.toInt(), buffer.getInt(0))
    }

    @Test
    fun i64Rmw32ReturnsZeroExtendedValue() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0xFFFFFFFF.toInt())

        val result = I64AtomicRmw32Add(memory, 0, 0L)

        assertEquals(0xFFFFFFFFL, result)
    }

    private fun linearMemory() = ByteBufferLinearMemory(LinearMemory.Pages(1u))
}

