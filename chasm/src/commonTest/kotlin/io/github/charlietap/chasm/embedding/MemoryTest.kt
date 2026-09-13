package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `passes linear memory config to the allocator`() {
        val store = publicStore()
        val memoryType = memoryType()
        val config = LinearMemoryConfig(prefault = true)
        var called = false

        val actual = memory(
            store = store,
            type = memoryType,
            config = config,
            allocator = { _, _, actualConfig ->
                assertEquals(config, actualConfig)
                called = true
                Address.Memory(0)
            },
        )

        assertTrue(called)
        assertEquals(publicMemory(ExternalValue.Memory(Address.Memory(0))), actual)
    }
}
