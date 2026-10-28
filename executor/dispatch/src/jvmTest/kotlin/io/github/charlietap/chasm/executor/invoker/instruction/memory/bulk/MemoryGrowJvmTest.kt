package io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk

import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryGrowIDispatcher
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MemoryGrowJvmTest {

    @Test
    fun `returns minus one when growth exceeds the jvm signed int byte limit`() {
        val maximumJvmPages = Int.MAX_VALUE / PAGE_SIZE
        val backing = ByteBufferLinearMemory(
            pages = LinearMemory.Pages(maximumJvmPages.toUInt()),
            maximumPages = LinearMemory.Pages(MAX_PAGES.toUInt()),
        )
        val memory = memoryInstance(
            type = memoryType(limits = limits(min = maximumJvmPages.toULong(), max = MAX_PAGES.toULong())),
            data = backing,
        )
        val vstack = vstack().apply { reserveDepth(1) }

        try {
            MemoryGrowIDispatcher(pagesToAdd = 1, destinationSlot = 0, memory = memory)
                .invoke(vstack, executionContext(vstack = vstack), 1)

            assertEquals(-1L, vstack.getFrameSlot(0))
            assertEquals(maximumJvmPages.toULong(), memory.type.limits.min)
            assertEquals(maximumJvmPages * PAGE_SIZE, memory.size)
            assertEquals(maximumJvmPages * PAGE_SIZE, backing.byteSize)
            assertSame(backing, memory.data)
        } finally {
            LinearMemoryDestructor(backing)
        }
    }
}
