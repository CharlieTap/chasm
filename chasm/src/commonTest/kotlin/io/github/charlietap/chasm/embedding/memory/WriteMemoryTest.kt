package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceByteWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteMemoryTest {

    @Test
    fun `can write a byte to a memory instance`() {

        val instance = memoryInstance()
        val store = store(memories = mutableListOf(instance))
        val address = memoryAddress()
        val offset = 118
        val byte: Byte = 117

        val byteWriter: MemoryInstanceByteWriter = { _instance, _value, _offset ->
            assertEquals(instance, _instance)
            assertEquals(byte, _value)
            assertEquals(offset, _offset)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeMemory(
            store = store,
            address = address,
            byteOffsetInMemory = offset,
            value = byte,
            byteWriter = byteWriter,
        )

        assertEquals(expected, actual)
    }
}
