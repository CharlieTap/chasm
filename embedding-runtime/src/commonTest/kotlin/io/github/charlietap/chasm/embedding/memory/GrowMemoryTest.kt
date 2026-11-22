package io.github.charlietap.chasm.embedding.memory

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.fixture.runtime.memory.NoOpLinearMemory
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.runtime.memory.OutOfMemoryError
import io.github.charlietap.chasm.type.MemoryType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GrowMemoryTest {

    @Test
    fun `can grow a memory instance`() {

        val grownMemory = FakeLinearMemory()
        val initialMemory = RecordingLinearMemory(grownMemory)
        val (memory, instance) = memoryBackedBy(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )

        val actual = growMemory(memory, 1)

        assertEquals(1, actual)
        assertEquals(1, initialMemory.growCalls)
        assertEquals(1, initialMemory.lastPagesToAdd)
        assertEquals(2u, instance.type.limits.min)
        assertEquals(PAGE_SIZE * 2, instance.size)
        assertSame(grownMemory, instance.data)
        assertEquals(2, sizeMemory(memory))
    }

    @Test
    fun `returns current size when memory growth is zero`() {

        val initialMemory = RecordingLinearMemory()
        val (memory, instance) = memoryBackedBy(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )

        val actual = growMemory(memory, 0)

        assertEquals(1, actual)
        assertEquals(0, initialMemory.growCalls)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    @Test
    fun `returns minus one when memory growth exceeds the maximum`() {

        val initialMemory = RecordingLinearMemory()
        val (memory, instance) = memoryBackedBy(
            type = memoryType(limits = limits(min = 1u, max = 1u)),
            data = initialMemory,
        )

        val actual = growMemory(memory, 1)

        assertEquals(-1, actual)
        assertEquals(0, initialMemory.growCalls)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    @Test
    fun `returns minus one for negative and overflowing growth`() {

        val initialMemory = RecordingLinearMemory()
        val (memory, instance) = memoryBackedBy(
            type = memoryType(limits = limits(min = 1u)),
            data = initialMemory,
        )

        assertEquals(-1, growMemory(memory, -1))
        assertEquals(-1, growMemory(memory, Int.MAX_VALUE))

        assertEquals(0, initialMemory.growCalls)
        assertEquals(1u, instance.type.limits.min)
        assertSame(initialMemory, instance.data)
    }

    @Test
    fun `returns minus one when backing memory rejects growth`() {

        listOf(IllegalArgumentException(), OutOfMemoryError()).forEach { failure ->

            val initialMemory = FailingLinearMemory(failure)
            val (memory, instance) = memoryBackedBy(
                type = memoryType(limits = limits(min = 1u, max = 3u)),
                data = initialMemory,
            )

            assertEquals(-1, growMemory(memory, 1))
            assertEquals(1u, instance.type.limits.min)
            assertEquals(PAGE_SIZE, instance.size)
            assertSame(initialMemory, instance.data)
        }
    }

    @Test
    fun `growing a real memory keeps its contents`() {

        val memory = memory(interpreterStore(), memoryType(limits = limits(min = 1u, max = 2u)))
        writeInt(memory, 7, 117)

        val actual = growMemory(memory, 1)

        assertEquals(1, actual)
        assertEquals(2, sizeMemory(memory))
        assertEquals(PAGE_SIZE * 2, memoryByteSize(memory))
        assertEquals(117, readInt(memory, 7))
        writeInt(memory, PAGE_SIZE + 4, 118)
        assertEquals(118, readInt(memory, PAGE_SIZE + 4))
    }

    private fun memoryBackedBy(
        type: MemoryType,
        data: LinearMemory,
    ): BackedMemory {

        val runtime = store()
        val memory = memory(interpreterStore(runtime), type)
        val instance = runtime.memories.single()
        instance.data = data
        instance.refresh()

        return BackedMemory(memory, instance)
    }

    private data class BackedMemory(
        val memory: Memory,
        val instance: MemoryInstance,
    )

    private class FakeLinearMemory : LinearMemory by NoOpLinearMemory

    private class FailingLinearMemory(
        private val failure: Throwable,
    ) : LinearMemory by NoOpLinearMemory {

        override val byteSize: Int = PAGE_SIZE

        override fun grow(pagesToAdd: Int): LinearMemory {
            throw failure
        }
    }

    private class RecordingLinearMemory(
        private val grownMemory: LinearMemory? = null,
    ) : LinearMemory by NoOpLinearMemory {

        var growCalls = 0
            private set

        var lastPagesToAdd = 0
            private set

        override fun grow(pagesToAdd: Int): LinearMemory {
            growCalls++
            lastPagesToAdd = pagesToAdd
            return grownMemory ?: this
        }
    }
}
