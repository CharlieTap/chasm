package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReader
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadMemoryTest {

    @Test
    fun `can read a byte from a memory instance`() {

        val instance = memoryInstance()
        val store = store(memories = mutableListOf(instance))
        val address = memoryAddress()
        val offset = 118
        val byte: Byte = 117

        val byteReader: MemoryInstanceByteReader = { _instance, _offset ->
            assertEquals(instance, _instance)
            assertEquals(offset, _offset)

            Ok(byte)
        }

        val expected: Result<Byte, ModuleRuntimeError> = Ok(byte)

        val actual = readMemory(
            store = store,
            address = address,
            byteOffsetInMemory = offset,
            byteReader = byteReader,
        )

        assertEquals(expected, actual)
    }
}
