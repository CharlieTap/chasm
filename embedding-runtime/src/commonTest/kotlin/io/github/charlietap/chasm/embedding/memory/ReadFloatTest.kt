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

class ReadFloatTest {

    @Test
    fun `can read a float from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val memoryPointer = 118
        val expectedFloat = 117.0f

        val bytesReader: BytesReader = { _memory, _buffer, _memoryPointer, _bytesToRead, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertContentEquals(ByteArray(4), _buffer)
            assertEquals(memoryPointer, _memoryPointer)
            assertEquals(4, _bytesToRead)
            assertEquals(0, _bufferPointer)

            byteArrayOf(0x00, 0x00, 0xEA.toByte(), 0x42)
        }

        val expected: Result<Float, ModuleTrapError> = Ok(expectedFloat)

        val actual = readFloat(
            store = store,
            memory = memory,
            memoryPointer = memoryPointer,
            bytesReader = bytesReader,
        )

        assertEquals(expected, actual)
    }
}
