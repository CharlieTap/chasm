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
import kotlin.test.assertEquals

class ReadBytesTest {

    @Test
    fun `can read bytes from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val memoryPointer = 118
        val bytesToRead = 2
        val buffer = byteArrayOf()
        val bufferPointer = 117
        val bytes: ByteArray = byteArrayOf(117, 118)

        val bytesReader: BytesReader = { _memory, _buffer, _memoryPointer, _bytesToRead, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertEquals(buffer, _buffer)
            assertEquals(memoryPointer, _memoryPointer)
            assertEquals(bytesToRead, _bytesToRead)
            assertEquals(bufferPointer, _bufferPointer)

            Ok(bytes)
        }

        val expected: Result<ByteArray, ModuleTrapError> = Ok(bytes)

        val actual = readBytes(
            store = store,
            memory = memory,
            buffer = buffer,
            memoryPointer = memoryPointer,
            bytesToRead = bytesToRead,
            bufferPointer = bufferPointer,
            bytesReader = bytesReader,
        )

        assertEquals(expected, actual)
    }
}
