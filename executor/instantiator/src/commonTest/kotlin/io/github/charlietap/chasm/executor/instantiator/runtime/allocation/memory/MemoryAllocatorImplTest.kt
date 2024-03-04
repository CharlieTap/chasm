package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.memory

import io.github.charlietap.chasm.executor.instantiator.allocation.memory.LinearMemoryFactory
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.fake.FakeLinearMemory
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryAllocatorImplTest {

    @Test
    fun `can allocate a memory instance`() {

        val memories = mutableListOf<MemoryInstance>()
        val store = store(
            memories = memories,
        )

        val min = 3
        val limits = limits(min.toUInt())
        val type = memoryType(limits = limits)

        val memory = FakeLinearMemory()
        val memoryFactory: LinearMemoryFactory = { factoryMin, factoryMax ->
            assertEquals(min, factoryMin)
            assertEquals(null, factoryMax)
            memory
        }

        val expected = MemoryInstance(
            type = type,
            data = memory,
        )

        val address = MemoryAllocatorImpl(store, type, memoryFactory)

        assertEquals(Address.Memory(0), address)
        assertEquals(expected, memories[0])
    }
}
