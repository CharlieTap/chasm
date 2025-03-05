package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.memory.read.StringReader
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadUtf8StringTest {

    @Test
    fun `can read a utf8 encoded string from a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(
            store(memories = mutableListOf(instance)),
        )
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val pointer = 1
        val stringLengthInBytes = 117

        val string = "Hello world"

        val stringReader: StringReader = { _memory, _pointer, _stringLengthInBytes ->
            assertEquals(instance.data, _memory)
            assertEquals(pointer, _pointer)
            assertEquals(stringLengthInBytes, _stringLengthInBytes)

            string
        }

        val expected: Result<String, ModuleTrapError> = Ok(string)

        val actual = readUtf8String(
            store = store,
            memory = memory,
            pointer = pointer,
            stringLengthInBytes = stringLengthInBytes,
            stringReader = stringReader,
        )

        assertEquals(expected, actual)
    }
}
