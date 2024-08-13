package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceByteWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteByteTest {

    @Test
    fun `can write a byte to a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 118
        val byte: Byte = 117

        val byteWriter: MemoryInstanceByteWriter = { _instance, _pointer, _byte ->
            assertEquals(instance, _instance)
            assertEquals(pointer, _pointer)
            assertEquals(byte, _byte)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeByte(
            store = store,
            memory = memory,
            pointer = pointer,
            byte = byte,
            byteWriter = byteWriter,
        )

        assertEquals(expected, actual)
    }
}
