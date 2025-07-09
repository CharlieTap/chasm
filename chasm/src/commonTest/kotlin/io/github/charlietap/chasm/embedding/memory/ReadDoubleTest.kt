package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ReadDoubleTest {

    @Test
    fun `can read a double from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val memoryPointer = 118
        val expectedDouble = 117.0

        val bytesReader: BytesReader = { _memory, _buffer, _memoryPointer, _bytesToRead, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertContentEquals(ByteArray(8), _buffer)
            assertEquals(memoryPointer, _memoryPointer)
            assertEquals(8, _bytesToRead)
            assertEquals(0, _bufferPointer)

            byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x5D, 0x40)
        }

        val expected: Result<Double, ModuleTrapError> = Ok(expectedDouble)

        val actual = readDouble(
            store = store,
            memory = memory,
            memoryPointer = memoryPointer,
            bytesReader = bytesReader,
        )

        assertEquals(expected, actual)
    }
}
