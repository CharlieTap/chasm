package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.memory

import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.memory.linearMemory
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryAllocatorTest {

    @Test
    fun `can allocate a memory instance`() {

        val memories = mutableListOf<MemoryInstance>()
        val store = store(
            memories = memories,
        )

        val min = 3
        val limits = limits(min.toUInt())
        val type = memoryType(limits = limits)

        val memory = linearMemory()
        val memoryFactory: LinearMemoryFactory = { pages ->
            assertEquals(LinearMemory.Pages(min.toUInt()), pages)
            memory
        }

        val expected = MemoryInstance(
            type = type,
            data = memory,
        )

        val address = MemoryAllocator(store, type, memoryFactory)

        assertEquals(Address.Memory(0), address)
        assertEquals(expected, memories[0])
    }
}
