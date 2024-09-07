package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MemoryInstanceByteReaderTest {

    @Test
    fun `can read bytes from linear memory using a buffer `() {

        val memoryArray = byteArrayOf(0, 0, 117, 118, 0, 0)
        val memory = ByteArrayLinearMemory(min = LinearMemory.Pages(1), max = LinearMemory.Pages(1), memory = memoryArray)
        val instance = memoryInstance(data = memory)

        val memoryPointer = 2
        val buffer = byteArrayOf(0, 0, 0, 0)
        val bufferPointer = 2

        val actual = MemoryInstanceBytesReader(
            instance = instance,
            buffer = buffer,
            memoryPointer = memoryPointer,
            bytesToRead = 2,
            bufferPointer = bufferPointer,
            linearMemoryInteractor = ::LinearMemoryInteractorImpl,
        )

        assertEquals(Ok(buffer), actual)
        assertContentEquals(byteArrayOf(0, 0, 117, 118), buffer)
    }

    @Test
    fun `error if returned if read is out of the memory's bounds `() {

        val memory = ByteArrayLinearMemory(min = LinearMemory.Pages(1))
        val instance = memoryInstance(data = memory)

        val memoryPointer = LinearMemory.PAGE_SIZE
        val buffer = byteArrayOf(0)
        val bufferPointer = 0

        val actual = MemoryInstanceBytesReader(
            instance = instance,
            buffer = buffer,
            memoryPointer = memoryPointer,
            bytesToRead = 1,
            bufferPointer = bufferPointer,
            linearMemoryInteractor = ::LinearMemoryInteractorImpl,
        )

        assertEquals(Err(InvocationError.MemoryOperationOutOfBounds), actual)
    }

    @Test
    fun `error if returned if read is out of the buffer's bounds `() {

        val memory = ByteArrayLinearMemory(min = LinearMemory.Pages(1))
        val instance = memoryInstance(data = memory)

        val memoryPointer = 0
        val buffer = byteArrayOf(0)
        val bufferPointer = 1

        val actual = MemoryInstanceBytesReader(
            instance = instance,
            buffer = buffer,
            memoryPointer = memoryPointer,
            bytesToRead = 1,
            bufferPointer = bufferPointer,
            linearMemoryInteractor = ::LinearMemoryInteractorImpl,
        )

        assertEquals(Err(InvocationError.MemoryOperationOutOfBounds), actual)
    }
}
