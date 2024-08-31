package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReader
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
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

        val byteReader: MemoryInstanceByteReader = { _instance, _offset ->
            assertEquals(instance, _instance)
            assertEquals(pointer, _offset)

            Ok(byte)
        }

        val expected: Result<Byte, ModuleTrapError> = Ok(byte)

        val actual = readByte(
            store = store,
            memory = memory,
            pointer = pointer,
            byteReader = byteReader,
        )

        assertEquals(expected, actual)
    }
}
