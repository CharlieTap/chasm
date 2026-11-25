package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import io.github.charlietap.chasm.config.Limits as RuntimeLimits

class SharedConfigTest {

    @Test
    fun aStoreHonoursTheConfigItsModuleWasCompiledWith() {

        val metered = dexModule("shared_config", RuntimeConfig(limits = RuntimeLimits(cpu = CpuLimits.Metered(1_000.fuel))))
        val limited = dexModule("shared_config", memoryLimits(MemoryLimits(linearMemory = 3.pages, tables = 32.bytes)))
        val trappingLimits = MemoryLimits(linearMemory = 3.pages, onGrowthFailure = GrowthFailureStrategy.Trap)
        val trapping = dexModule("shared_config", memoryLimits(trappingLimits))
        val meteredStore = store(metered)
        val limitedStore = store(limited)
        val trappingStore = store(trapping)

        assertTrapped(TrapReason.FUEL_EXHAUSTED, invoke(instantiate(meteredStore, metered), "spin"))
        assertEquals(0L, remainingFuel(meteredStore))

        val growing = instantiate(limitedStore, limited)
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(growing, "memory-grow", listOf(I32(2))))
        assertEquals(ChasmResult.Success(listOf(I32(-1))), invoke(growing, "memory-grow", listOf(I32(1))))
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(growing, "table-grow", listOf(I32(3))))
        assertEquals(ChasmResult.Success(listOf(I32(-1))), invoke(growing, "table-grow", listOf(I32(1))))

        assertFailsWith<IllegalArgumentException> {
            memory(limitedStore, MemoryType(AddressType.I32, Limits(4u), SharedStatus.Unshared))
        }

        val trapped = instantiate(trappingStore, trapping)
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(trapped, "memory-grow", listOf(I32(2))))
        assertTrapped(TrapReason.MEMORY_GROW_FAILED, invoke(trapped, "memory-grow", listOf(I32(1))))
    }

    @Test
    fun modulesCompiledWithOtherSettingsAreUnlinkable() {

        val metered = RuntimeConfig(limits = RuntimeLimits(cpu = CpuLimits.Metered(1_000.fuel)))
        val limited = memoryLimits(MemoryLimits(linearMemory = 3.pages))
        val meteredStore = store(dexModule("shared_config", metered))
        val limitedStore = store(dexModule("shared_config", limited))

        val unmetered = unlinkable(meteredStore, dexModule("shared_config"))
        val widened = unlinkable(limitedStore, dexModule("shared_config", memoryLimits(MemoryLimits(linearMemory = 4.pages))))
        instantiate(meteredStore, dexModule("shared_config", metered.copy(stacktraces = true)))

        assertContains(unmetered.message, "fuel")
        assertContains(widened.message, "linearMemory")
    }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = RuntimeLimits(memory = memory))
    }

    private fun instantiate(
        store: Store,
        module: Module,
    ): Instance {
        return runBlocking { instance(store, module) }.expect("instance")
    }

    private fun unlinkable(
        store: Store,
        module: Module,
    ): ExecutionError.Unlinkable {

        val result = runBlocking { instance(store, module) }

        return assertIs<ExecutionError.Unlinkable>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    private fun assertTrapped(
        reason: TrapReason,
        result: ChasmResult<*, *>,
    ) {

        val error = assertIs<ChasmResult.Error<*>>(result).error

        assertEquals(reason, assertIs<ExecutionError.Trapped>(error).trap.reason)
    }
}
