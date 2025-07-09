package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class WriteLongTest {

    @Test
    fun `can write a long to a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 118
        val value = 117L

        val bytesWriter: BytesWriter = { _memory, _size, _buffer, _pointer, _bytes, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertEquals(instance.size, _size)
            assertContentEquals(byteArrayOf(0x75, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), _buffer)
            assertEquals(pointer, _pointer)
            assertEquals(8, _bytes)
            assertEquals(0, _bufferPointer)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeLong(
            store = store,
            memory = memory,
            pointer = pointer,
            value = value,
            bytesWriter = bytesWriter,
        )

        assertEquals(expected, actual)
    }
}
