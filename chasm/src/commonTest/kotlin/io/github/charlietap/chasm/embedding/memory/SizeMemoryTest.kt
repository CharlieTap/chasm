package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.memory.size.MemoryInstanceSizer
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import kotlin.test.Test
import kotlin.test.assertEquals

class SizeMemoryTest {

    @Test
    fun `can size a memory instance`() {

        val instance = memoryInstance()
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))
        val size = 117

        val sizer: MemoryInstanceSizer = { _instance ->
            assertEquals(instance, _instance)

            size
        }

        val expected: Result<Int, ModuleTrapError> = Ok(size)

        val actual = sizeMemory(
            store = store,
            memory = memory,
            sizer = sizer,
        )

        assertEquals(expected, actual)
    }
}
