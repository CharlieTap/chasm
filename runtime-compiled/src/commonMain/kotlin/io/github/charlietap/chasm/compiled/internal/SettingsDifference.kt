package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig

internal class SettingsDifference(
    private val moduleConfig: RuntimeConfig,
    private val storeConfig: RuntimeConfig,
) {

    fun describe(): String? {

        val moduleMemory = moduleConfig.limits.memory
        val storeMemory = storeConfig.limits.memory

        return when {
            moduleConfig.limits.cpu != storeConfig.limits.cpu ->
                "fuel ${fuel(moduleConfig.limits.cpu)} but the store has fuel ${fuel(storeConfig.limits.cpu)}"
            moduleConfig.interrupts != storeConfig.interrupts ->
                "interrupts ${moduleConfig.interrupts} but the store has interrupts ${storeConfig.interrupts}"
            moduleMemory.linearMemory != storeMemory.linearMemory ->
                "linearMemory ${bytes(moduleMemory.linearMemory)} but the store allows ${bytes(storeMemory.linearMemory)}"
            moduleMemory.gcHeap != storeMemory.gcHeap ->
                "gcHeap ${bytes(moduleMemory.gcHeap)} but the store allows ${bytes(storeMemory.gcHeap)}"
            moduleMemory.tables != storeMemory.tables ->
                "tables ${bytes(moduleMemory.tables)} but the store allows ${bytes(storeMemory.tables)}"
            moduleMemory.stack != storeMemory.stack ->
                "stack ${moduleMemory.stack ?: "default"} but the store allows ${storeMemory.stack ?: "default"}"
            moduleMemory.onGrowthFailure != storeMemory.onGrowthFailure ->
                "onGrowthFailure ${moduleMemory.onGrowthFailure} but the store has onGrowthFailure ${storeMemory.onGrowthFailure}"
            else -> null
        }
    }

    private fun fuel(cpu: CpuLimits): String {
        return when (cpu) {
            is CpuLimits.Metered -> cpu.fuel.units.toString()
            CpuLimits.Unmetered -> "unmetered"
        }
    }

    private fun bytes(value: Bytes?): String {
        return value?.toString() ?: "no limit"
    }
}
