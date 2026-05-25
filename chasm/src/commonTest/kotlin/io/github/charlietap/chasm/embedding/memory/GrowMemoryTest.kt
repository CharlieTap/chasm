package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.memory.linearMemory
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GrowMemoryTest {

    @Test
    fun `can grow a memory instance`() {

        val initialMemory = linearMemory()
        val grownMemory = FakeLinearMemory
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val grower: LinearMemoryGrower = { _memory, _pagesToAdd ->
            assertSame(initialMemory, _memory)
            assertEquals(1, _pagesToAdd)

            grownMemory
        }

        val expected: Result<Int, ModuleTrapError> = Ok(1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 1,
            grower = grower,
        )

        assertEquals(expected, actual)
        assertEquals(2u, instance.type.limits.min)
        assertEquals(PAGE_SIZE * 2, instance.size)
        assertSame(grownMemory, instance.data)
    }

    @Test
    fun `returns current size when memory growth is zero`() {

        val initialMemory = linearMemory()
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val grower: LinearMemoryGrower = { _, _ ->
            error("grower should not be called when growth is zero")
        }

        val expected: Result<Int, ModuleTrapError> = Ok(1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 0,
            grower = grower,
        )

        assertEquals(expected, actual)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    @Test
    fun `returns minus one when memory growth exceeds the maximum`() {

        val initialMemory = linearMemory()
        val instance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 1u)),
            data = initialMemory,
        )
        val store = publicStore(store(memories = mutableListOf(instance)))
        val address = memoryAddress()
        val memory = publicMemory(memoryExternalValue(address))

        val grower: LinearMemoryGrower = { _, _ ->
            error("grower should not be called when growth exceeds the maximum")
        }

        val expected: Result<Int, ModuleTrapError> = Ok(-1)

        val actual = growMemory(
            store = store,
            memory = memory,
            pagesToAdd = 1,
            grower = grower,
        )

        assertEquals(expected, actual)
        assertEquals(1u, instance.type.limits.min)
        assertEquals(PAGE_SIZE, instance.size)
        assertSame(initialMemory, instance.data)
    }

    private object FakeLinearMemory : LinearMemory
}
