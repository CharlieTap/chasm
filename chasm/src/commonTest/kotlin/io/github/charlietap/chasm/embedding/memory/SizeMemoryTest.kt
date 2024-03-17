package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.size.MemoryInstanceSizer
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class SizeMemoryTest {

    @Test
    fun `can size a memory instance`() {

        val instance = memoryInstance()
        val store = store(memories = mutableListOf(instance))
        val address = memoryAddress()
        val size = 117

        val sizer: MemoryInstanceSizer = { _instance ->
            assertEquals(instance, _instance)

            Ok(size)
        }

        val expected: Result<Int, ModuleRuntimeError> = Ok(size)

        val actual = sizeMemory(
            store = store,
            address = address,
            sizer = sizer,
        )

        assertEquals(expected, actual)
    }
}
