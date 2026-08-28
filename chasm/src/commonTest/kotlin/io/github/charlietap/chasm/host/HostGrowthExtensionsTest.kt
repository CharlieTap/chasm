package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HostGrowthExtensionsTest {

    @Test
    fun `grows memory from within its host DSL`() {
        val decoyMemory = LinearMemoryFactory(LinearMemory.Pages(1u))
        val memory = LinearMemoryFactory(LinearMemory.Pages(1u))
        val targetInstance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 3u)),
            data = memory,
        )
        val store = store(
            memories = mutableListOf(
                memoryInstance(data = decoyMemory),
                targetInstance,
            ),
        )
        val module = moduleInstance(
            memAddresses = mutableListOf(
                memoryAddress(0),
                memoryAddress(1),
            ),
        )
        val resources = executionContext(store = store)

        context(module, resources) {
            withMemory(1) {
                assertEquals(1, grow(1))
                assertEquals(2, grow(0))
                assertSame(memory, this)
                assertEquals(PAGE_SIZE * 2, byteSize)
            }
        }

        assertSame(memory, targetInstance.data)
        assertEquals(2u, targetInstance.type.limits.min)
        assertEquals(PAGE_SIZE * 2, targetInstance.size)
    }

    @Test
    fun `memory growth returns minus one without changing the memory when it exceeds the maximum`() {
        val memory = LinearMemoryFactory(LinearMemory.Pages(1u))
        val memoryInstance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 1u)),
            data = memory,
        )
        val store = store(memories = mutableListOf(memoryInstance))
        val module = moduleInstance(memAddresses = mutableListOf(memoryAddress()))
        val resources = executionContext(store = store)

        val result = context(module, resources) {
            withMemory(0) { grow(1) }
        }

        assertEquals(-1, result)
        assertSame(memory, memoryInstance.data)
        assertEquals(1u, memoryInstance.type.limits.min)
        assertEquals(PAGE_SIZE, memoryInstance.size)
    }

    @Test
    fun `memory growth rejects a negative page count without changing the memory`() {
        val memory = LinearMemoryFactory(LinearMemory.Pages(1u))
        val memoryInstance = memoryInstance(
            type = memoryType(limits = limits(min = 1u, max = 2u)),
            data = memory,
        )
        val store = store(memories = mutableListOf(memoryInstance))
        val module = moduleInstance(memAddresses = mutableListOf(memoryAddress()))
        val resources = executionContext(store = store)

        val result = context(module, resources) {
            withMemory(0) { grow(-1) }
        }

        assertEquals(-1, result)
        assertSame(memory, memoryInstance.data)
        assertEquals(1u, memoryInstance.type.limits.min)
        assertEquals(PAGE_SIZE, memoryInstance.size)
    }

    @Test
    fun `grows table from within its host DSL`() {
        val tableInstance = tableInstance(
            type = tableType(limits = limits(min = 2u, max = 4u)),
            elements = longArrayOf(7L, 9L),
        )
        val store = store(tables = mutableListOf(tableInstance))
        val module = moduleInstance(tableAddresses = mutableListOf(tableAddress()))
        val resources = executionContext(store = store)

        context(module, resources) {
            withTable(0) {
                assertEquals(2, grow(2, 11L))
                assertEquals(4, size)
            }
        }

        assertContentEquals(longArrayOf(7L, 9L, 11L, 11L), tableInstance.elements)
        assertEquals(4u, tableInstance.type.limits.min)
    }

    @Test
    fun `table growth returns minus one without changing the table when it exceeds the maximum`() {
        val elements = longArrayOf(7L, 9L)
        val tableInstance = tableInstance(
            type = tableType(limits = limits(min = 2u, max = 2u)),
            elements = elements,
        )
        val store = store(tables = mutableListOf(tableInstance))
        val module = moduleInstance(tableAddresses = mutableListOf(tableAddress()))
        val resources = executionContext(store = store)

        val result = context(module, resources) {
            withTable(0) { grow(1, 11L) }
        }

        assertEquals(-1, result)
        assertSame(elements, tableInstance.elements)
        assertEquals(2u, tableInstance.type.limits.min)
    }
}
