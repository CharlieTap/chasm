package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun `can allocate a memory in the store and return an external value`() {

        val store = store()
        val memoryType = memoryType()

        val expected = ExternalValue.Memory(Address.Memory(0))

        val actual = memory(store, memoryType)

        assertEquals(expected, actual)
        assertEquals(memoryType, store.memories[0].type)
    }
}
