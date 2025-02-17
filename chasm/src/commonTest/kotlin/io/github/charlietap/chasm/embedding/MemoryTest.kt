package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ir.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun `can allocate a memory in the store and return an external value`() {

        val store = publicStore()
        val memoryType = memoryType()

        val expectedExternalValue = ExternalValue.Memory(Address.Memory(0))
        val expected = publicMemory(expectedExternalValue)

        val actual = memory(store, memoryType)

        assertEquals(expected, actual)
        assertEquals(memoryType(), store.store.memories[0].type)
    }
}
