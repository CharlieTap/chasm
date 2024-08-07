package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceBytesWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteBytesTest {

    @Test
    fun `can write bytes to a memory instance`() {

        val instance = memoryInstance()
        val store = store(memories = mutableListOf(instance))
        val address = memoryAddress()
        val pointer = 118
        val bytes: ByteArray = byteArrayOf(117, 118)

        val bytesWriter: MemoryInstanceBytesWriter = { _instance, _pointer, _bytes ->
            assertEquals(instance, _instance)
            assertEquals(pointer, _pointer)
            assertEquals(bytes, _bytes)

            Ok(Unit)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeBytes(
            store = store,
            address = address,
            pointer = pointer,
            bytes = bytes,
            bytesWriter = bytesWriter,
        )

        assertEquals(expected, actual)
    }
}
