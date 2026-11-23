package io.github.charlietap.chasm.jit

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
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import io.github.charlietap.chasm.config.Limits as RuntimeLimits

class SharedConfigTest {

    @Test
    fun aJitStoreHonoursTheSharedConfig() {

        val metered = store(RuntimeConfig(limits = RuntimeLimits(cpu = CpuLimits.Metered(1_000.fuel))))
        val limited = store(memoryLimits(MemoryLimits(linearMemory = 3.pages, tables = 32.bytes)))
        val trapping = store(memoryLimits(MemoryLimits(linearMemory = 3.pages, onGrowthFailure = GrowthFailureStrategy.Trap)))

        assertTrapped(TrapReason.FUEL_EXHAUSTED, invoke(instantiate(metered), "spin"))
        assertEquals(0L, remainingFuel(metered))

        val growing = instantiate(limited)
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(growing, "memory-grow", listOf(I32(2))))
        assertEquals(ChasmResult.Success(listOf(I32(-1))), invoke(growing, "memory-grow", listOf(I32(1))))
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(growing, "table-grow", listOf(I32(3))))
        assertEquals(ChasmResult.Success(listOf(I32(-1))), invoke(growing, "table-grow", listOf(I32(1))))

        assertFailsWith<IllegalArgumentException> {
            memory(limited, MemoryType(AddressType.I32, Limits(4u), SharedStatus.Unshared))
        }

        val trapped = instantiate(trapping)
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(trapped, "memory-grow", listOf(I32(2))))
        assertTrapped(TrapReason.MEMORY_GROW_FAILED, invoke(trapped, "memory-grow", listOf(I32(1))))
    }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = RuntimeLimits(memory = memory))
    }

    private fun instantiate(store: Store): Instance {

        val module = runBlocking { module(resourceBytes("modules/shared_config.wasm")) }.expect("module")

        return runBlocking { instance(store, module) }.expect("instance")
    }

    private fun assertTrapped(
        reason: TrapReason,
        result: ChasmResult<*, *>,
    ) {

        val error = assertIs<ChasmResult.Error<*>>(result).error

        assertEquals(reason, assertIs<ExecutionError.Trapped>(error).trap.reason)
    }
}
