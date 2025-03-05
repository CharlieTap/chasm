package io.github.charlietap.chasm.executor.invoker.drop

import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.memory.linearMemory
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.type.Limits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemoryInstanceDropperTest {

    @Test
    fun `calling MemoryInstanceDropper sets bounds to zero and invokes linear memory destructor`() {
        val linearMemory = linearMemory()
        val memoryType = memoryType(
            limits = limits(4u, 8u),
            shared = unsharedStatus(),
        )
        val memoryInstance = memoryInstance(
            type = memoryType,
            data = linearMemory,
        )
        assertEquals(PAGE_SIZE * 4, memoryInstance.size) // set size cache

        var destructorCalled = false
        val destructor: LinearMemoryDestructor = { _data ->
            assertEquals(linearMemory, _data)
            destructorCalled = true
        }

        MemoryInstanceDropper(memoryInstance, destructor)

        assertEquals(Limits(0u, 0u), memoryInstance.type.limits)
        assertEquals(0, memoryInstance.size) // test cache refresh
        assertTrue(destructorCalled)
    }
}
