package io.github.charlietap.chasm.embedding.memory

import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.memory.NoOpLinearMemory
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GrowMemoryTest {

    @Test
    fun `can grow a memory instance`() {

        val grownMemory = FakeLinearMemory
        val initialMemory = RecordingLinearMemory(grownMemory)
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val expected = ChasmResult.Success(1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 1,
        )

        assertEquals(expected, actual)
        assertEquals(1, initialMemory.growCalls)
        assertEquals(1, initialMemory.lastPagesToAdd)
        assertEquals(2u, instance.type.limits.min)
        assertEquals(PAGE_SIZE * 2, instance.size)
        assertSame(grownMemory, instance.data)
    }

    @Test
    fun `returns current size when memory growth is zero`() {

        val initialMemory = RecordingLinearMemory()
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val expected = ChasmResult.Success(1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 0,
        )

        assertEquals(expected, actual)
        assertEquals(0, initialMemory.growCalls)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    @Test
    fun `returns minus one when memory growth exceeds the maximum`() {

        val initialMemory = RecordingLinearMemory()
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 1u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val expected = ChasmResult.Success(-1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 1,
        )

        assertEquals(expected, actual)
        assertEquals(0, initialMemory.growCalls)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    private object FakeLinearMemory : LinearMemory by NoOpLinearMemory

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
