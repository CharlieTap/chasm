package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.write.BytesWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertContentEquals
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

        val bytesWriter: BytesWriter = { _memory, _size, _buffer, _pointer, _bytes, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertEquals(instance.size, _size)
            assertContentEquals(byteArrayOf(byte), _buffer)
            assertEquals(pointer, _pointer)
            assertEquals(1, _bytes)
            assertEquals(0, _bufferPointer)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeByte(
            store = store,
            memory = memory,
            pointer = pointer,
            byte = byte,
            bytesWriter = bytesWriter,
        )

        assertEquals(expected, actual)
    }
}
