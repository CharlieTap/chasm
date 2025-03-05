package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.memory.write.StringWriter
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteUtf8StringTest {

    @Test
    fun `can write a utf8 encoded string to a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(
            store(memories = mutableListOf(instance)),
        )
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 1

        val string = "Hello world"

        val stringWriter: StringWriter = { _memory, _pointer, _string ->
            assertEquals(instance.data, _memory)
            assertEquals(pointer, _pointer)
            assertEquals(string, _string)
        }

        val expected: Result<Unit, ModuleTrapError> = Ok(Unit)

        val actual = writeUtf8String(
            store = store,
            memory = memory,
            pointer = pointer,
            string = string,
            stringWriter = stringWriter,
        )

        assertEquals(expected, actual)
    }
}
