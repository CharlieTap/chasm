package io.github.charlietap.chasm.executor.instantiator.runtime.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.instantiator.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.MAX_PAGES
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayLinearMemoryTest {

    @Test
    fun `can memory is initialised with the correct size`() {

        val min = LinearMemory.Pages(2)

        val memory = ByteArrayLinearMemory(min)

        assertEquals(PAGE_SIZE * min.amount, memory.memory.size)
    }

    @Test
    fun `can grow linear memory by a provided amount of pages`() {

        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(3)
        val additional = LinearMemory.Pages(2)
        val memory = ByteArrayLinearMemory(min, max)

        val newMin = min.amount + additional.amount
        val newMinPages = LinearMemory.Pages(newMin)
        val expectedSize = PAGE_SIZE * newMin
        val expected = ByteArrayLinearMemory(newMinPages, max, memory.memory.copyOf(expectedSize))

        val actual = memory.grow(additional)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot grow memory beyond max`() {

        val min = LinearMemory.Pages(2)
        val max = LinearMemory.Pages(4)
        val additional = LinearMemory.Pages(3)
        val memory = ByteArrayLinearMemory(min, max)

        val expected = Err(
            InvocationError.MemoryGrowExceedsLimits(additional.amount, max.amount),
        )

        val actual = memory.grow(additional)

        assertEquals(expected, actual)
    }

    @Test
    fun `cannot grow memory beyond max pages`() {

        val min = LinearMemory.Pages(1)
        val additional = LinearMemory.Pages(MAX_PAGES)
        val memory = ByteArrayLinearMemory(min)

        val expected = Err(
            InvocationError.MemoryGrowExceedsLimits(additional.amount, MAX_PAGES),
        )

        val actual = memory.grow(additional)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read an int from linear memory`() {

        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117

        expected.copyInto(memory.memory, offset)

        val actual = memory.readInt(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read an int from linear memory that's out of bounds`() {

        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - Int.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readInt(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a uint from linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117u

        expected.copyInto(memory.memory, offset)

        val actual = memory.readUInt(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read a uint from linear memory that's out of bounds`() {
        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - UInt.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readUInt(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a long from linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117L

        expected.copyInto(memory.memory, offset)

        val actual = memory.readLong(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read a long from linear memory that's out of bounds`() {
        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - Long.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readLong(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a ulong from linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117uL

        expected.copyInto(memory.memory, offset)

        val actual = memory.readULong(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read a ulong from linear memory that's out of bounds`() {
        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - ULong.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readULong(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a float from linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117.0f

        expected.copyInto(memory.memory, offset)

        val actual = memory.readFloat(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read a float from linear memory that's out of bounds`() {
        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - Float.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readFloat(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a double from linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)

        val offset = 0
        val expected = 117.0

        expected.copyInto(memory.memory, offset)

        val actual = memory.readDouble(offset)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot read a double from linear memory that's out of bounds`() {
        val min = LinearMemory.Pages(1)
        val max = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min, max)
        val offset = PAGE_SIZE - Double.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.readDouble(offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write an int to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117

        val actual = memory.writeInt(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readInt(offset))
    }

    @Test
    fun `cannot write an int beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - Int.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeInt(117, offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a uint to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117u

        val actual = memory.writeUInt(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readUInt(offset))
    }

    @Test
    fun `cannot write a uint beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - UInt.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeUInt(117u, offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a long to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117L

        val actual = memory.writeLong(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readLong(offset))
    }

    @Test
    fun `cannot write a long beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - Long.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeLong(117L, offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a ulong to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117uL

        val actual = memory.writeULong(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readULong(offset))
    }

    @Test
    fun `cannot write a ulong beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - ULong.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeULong(117uL, offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a float to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117.0f

        val actual = memory.writeFloat(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readFloat(offset))
    }

    @Test
    fun `cannot write a float beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - Float.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeFloat(117.0f, offset)

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a double to linear memory`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = 0

        val value = 117.0

        val actual = memory.writeDouble(value, offset)

        assertEquals(Ok(Unit), actual)
        assertEquals(Ok(value), memory.readDouble(offset))
    }

    @Test
    fun `cannot write a double beyond linear memory bounds`() {
        val min = LinearMemory.Pages(1)
        val memory = ByteArrayLinearMemory(min)
        val offset = PAGE_SIZE - Double.SIZE_BYTES + 1

        val expected = Err(InvocationError.MemoryOperationOutOfBounds)

        val actual = memory.writeDouble(117.0, offset)

        assertEquals(expected, actual)
    }
}
