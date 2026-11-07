package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.config.StoreLimits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LimitsTest {

    @Test
    fun `sizes count in 1024s and convert to pages and table elements`() {
        val limits = MemoryLimits.Constrained(linearMemory = 1.MB, tables = 64.KB)

        assertEquals(16, limits.linearMemoryPages)
        assertEquals(8_192, limits.tableElements)
        assertEquals(64 * 1024L, 1.wasmPages.bytes)
        assertEquals("1 GB", 1.GB.toString())
    }

    @Test
    fun `invalid limits are rejected where they are set`() {
        assertFailsWith<IllegalArgumentException> { (-1).fuel }
        assertFailsWith<IllegalArgumentException> { MemoryLimits.Constrained(stack = 0.bytes) }
        assertFailsWith<IllegalArgumentException> { MemoryLimits.Constrained(stack = 257.MB) }
    }

    @Test
    fun `fuel saturates instead of overflowing`() {
        assertEquals(Long.MAX_VALUE.fuel, Long.MAX_VALUE.fuel + 1.fuel)
    }

    @Test
    fun `unconstrained memory keeps the store defaults`() {
        assertEquals(StoreLimits(), MemoryLimits.Unconstrained().toStoreLimits())
    }

    @Test
    fun `constrained memory caps each kind of memory`() {
        val memory = MemoryLimits.Constrained(linearMemory = 1.MB, gcHeap = 2.MB, tables = 64.KB, stack = 512.KB)

        val expected = StoreLimits(
            gcHeapBytes = 2L * 1024 * 1024,
            memoryPages = 16,
            tableElements = 8_192,
            trapOnGrowFailure = true,
            stackBytes = 512L * 1024,
        )

        assertEquals(expected, memory.toStoreLimits())
    }

    @Test
    fun `tables larger than a table can hold are capped at the most elements a table has`() {
        val limits = MemoryLimits.Constrained(tables = 32.GB).toStoreLimits()

        assertEquals(Int.MAX_VALUE, limits.tableElements)
    }
}
