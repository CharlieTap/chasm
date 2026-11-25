package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Fuel
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig

internal class RuntimeSettings {

    fun config(compiled: CompiledModule): RuntimeConfig? {

        val fuel = compiled.fuel()
        val linearMemory = compiled.linearMemoryBytes()
        val gcHeap = compiled.gcHeapBytes()
        val tables = compiled.tableBytes()
        val stack = compiled.stackBytes()
        if (listOf(fuel, linearMemory, gcHeap, tables, stack).any { value -> value < UNSET_LONG }) {
            return null
        }

        return RuntimeConfig(
            interrupts = compiled.interrupts(),
            stacktraces = compiled.stacktraces(),
            limits = Limits(
                cpu = if (fuel == UNSET_LONG) CpuLimits.Unmetered else CpuLimits.Metered(Fuel(fuel)),
                memory = MemoryLimits(
                    linearMemory = bytes(linearMemory),
                    gcHeap = bytes(gcHeap),
                    tables = bytes(tables),
                    stack = bytes(stack),
                    onGrowthFailure = if (compiled.trapOnGrowthFailure()) GrowthFailureStrategy.Trap else GrowthFailureStrategy.Continue,
                ),
            ),
        )
    }

    private fun bytes(value: Long): Bytes? {
        return if (value == UNSET_LONG) null else Bytes(value)
    }
}
