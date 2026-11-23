package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoreTest {

    @Test
    fun `a metered store starts with its fuel`() {

        val store = Store(config = RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(1_234.fuel))))

        assertTrue(store.fuel.metered)
        assertEquals(1_234L, store.fuel.remaining)
    }

    @Test
    fun `a memory limit is a whole number of pages`() {

        val store = Store(config = memoryLimits(MemoryLimits(linearMemory = 100_000.bytes)))

        assertEquals(1, store.memoryPageLimit)
        assertNull(Store().memoryPageLimit)
    }

    @Test
    fun `a table limit is eight bytes an element`() {

        val store = Store(config = memoryLimits(MemoryLimits(tables = 33.bytes)))

        assertEquals(4, store.tableElementLimit)
        assertNull(Store().tableElementLimit)
    }

    @Test
    fun `growth failure traps only when asked`() {

        val trapping = Store(config = memoryLimits(MemoryLimits(onGrowthFailure = GrowthFailureStrategy.Trap)))

        assertTrue(trapping.trapOnGrowthFailure)
        assertFalse(Store().trapOnGrowthFailure)
    }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = Limits(memory = memory))
    }
}
