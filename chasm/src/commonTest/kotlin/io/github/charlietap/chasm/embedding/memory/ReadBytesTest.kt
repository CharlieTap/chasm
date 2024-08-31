package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceBytesReader
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadBytesTest {

    @Test
    fun `can read bytes from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 118
        val numberOfBytes = 2
        val bytes: ByteArray = byteArrayOf(117, 118)

        val bytesReader: MemoryInstanceBytesReader = { _instance, _pointer, _numberOfBytes ->
            assertEquals(instance, _instance)
            assertEquals(pointer, _pointer)
            assertEquals(numberOfBytes, _numberOfBytes)

            Ok(bytes)
        }

        val expected: Result<ByteArray, ModuleTrapError> = Ok(bytes)

        val actual = readBytes(
            store = store,
            memory = memory,
            pointer = pointer,
            numberOfBytes = numberOfBytes,
            bytesReader = bytesReader,
        )

        assertEquals(expected, actual)
    }
}
