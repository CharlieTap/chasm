package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomicLoadsJvmTest {

    @Test
    fun i32LoadReturnsValueStoredInMemory() {
        val memory = ByteBufferLinearMemory(LinearMemory.Pages(1u))
        memory.memory.putInt(0, 0x1234_5678)

        val result = I32AtomicLoad(memory, 0)

        assertEquals(0x1234_5678, result)
    }

    @Test
    fun i32LoadReadsValueAtNonZeroOffset() {
        val memory = ByteBufferLinearMemory(LinearMemory.Pages(1u))
        val buffer = memory.memory
        val address = 4
        val expected = 0xCAFE_BABE.toInt()

        buffer.putInt(address, expected)

        val result = I32AtomicLoad(memory, address)

        assertEquals(expected, result)
    }
}


