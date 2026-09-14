package io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.memory.NoOpLinearMemory
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.runtime.memory.OutOfMemoryError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MemoryGrowExecutorTest {

    @Test
    fun `returns minus one for negative and overflowing growth`() {
        val backing = RecordingLinearMemory()
        val memory = memoryInstance(
            type = memoryType(limits = limits(min = 1u)),
            data = backing,
        )

        listOf(-1, Int.MAX_VALUE).forEach { pagesToAdd ->
            val vstack = vstack().apply {
                reserveDepth(2)
                setFrameSlot(0, pagesToAdd.toLong())
            }

            MemoryGrowExecutor(
                vstack = vstack,
                context = executionContext(vstack = vstack),
                instruction = MemoryInstruction.MemoryGrowS(0, 1, memory, MAX_PAGES),
            )

            assertEquals(-1L, vstack.getFrameSlot(1))
        }

        assertEquals(0, backing.growCalls)
        assertEquals(1u, memory.type.limits.min)
        assertSame(backing, memory.data)
    }

    @Test
    fun `returns minus one when backing memory rejects growth`() {
        listOf(IllegalArgumentException(), OutOfMemoryError()).forEach { failure ->
            val backing = FailingLinearMemory(failure)
            val memory = memoryInstance(
                type = memoryType(limits = limits(min = 1u, max = 3u)),
                data = backing,
            )
            val vstack = vstack().apply { reserveDepth(1) }

            MemoryGrowExecutor(
                vstack = vstack,
                context = executionContext(vstack = vstack),
                instruction = MemoryInstruction.MemoryGrowI(1, 0, memory, 3),
            )

            assertEquals(-1L, vstack.getFrameSlot(0))
            assertEquals(1u, memory.type.limits.min)
            assertSame(backing, memory.data)
        }
    }

    private class RecordingLinearMemory : LinearMemory by NoOpLinearMemory {
        var growCalls = 0
            private set

        override fun grow(pagesToAdd: Int): LinearMemory {
            growCalls++
            return this
        }
    }

    private class FailingLinearMemory(
        private val failure: Throwable,
    ) : LinearMemory by NoOpLinearMemory {
        override val byteSize: Int = PAGE_SIZE

        override fun grow(pagesToAdd: Int): LinearMemory = throw failure
    }
}
