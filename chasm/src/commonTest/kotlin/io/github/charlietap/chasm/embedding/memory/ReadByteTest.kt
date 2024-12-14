package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.read.BytesReader
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ReadByteTest {

    @Test
    fun `can read a byte from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 118
        val byte: Byte = 117

        val bytesReader: BytesReader = { _memory, _buffer, _memoryPointer, _bytesToRead, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertContentEquals(ByteArray(1), _buffer)
            assertEquals(pointer, _memoryPointer)
            assertEquals(1, _bytesToRead)
            assertEquals(0, _bufferPointer)

            Ok(byteArrayOf(byte))
        }

        val expected: Result<Byte, ModuleTrapError> = Ok(byte)

        val actual = readByte(
            store = store,
            memory = memory,
            pointer = pointer,
            bytesReader = bytesReader,
        )

        assertEquals(expected, actual)
    }
}
