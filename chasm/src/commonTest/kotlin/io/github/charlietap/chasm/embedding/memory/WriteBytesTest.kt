package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.write.BytesWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteBytesTest {

    @Test
    fun `can write bytes to a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 118
        val buffer: ByteArray = byteArrayOf(117, 118)

        val bytesWriter: BytesWriter = { _memory, _size, _buffer, _pointer, _bytes, _bufferPointer ->
            assertEquals(instance.data, _memory)
            assertEquals(instance.size, _size)
            assertEquals(buffer, _buffer)
            assertEquals(pointer, _pointer)
            assertEquals(2, _bytes)
            assertEquals(0, _bufferPointer)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeBytes(
            store = store,
            memory = memory,
            pointer = pointer,
            bytes = buffer,
            bytesWriter = bytesWriter,
        )

        assertEquals(expected, actual)
    }
}
