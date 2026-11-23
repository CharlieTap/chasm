package io.github.charlietap.chasm.config

data class Limits(
    val cpu: CpuLimits = CpuLimits.Unmetered,
    val memory: MemoryLimits = MemoryLimits(),
)

sealed interface CpuLimits {

    data object Unmetered : CpuLimits

    data class Metered(val fuel: Fuel) : CpuLimits
}

data class MemoryLimits(
    val linearMemory: Bytes? = null,
    val gcHeap: Bytes? = null,
    val tables: Bytes? = null,
    val stack: Bytes? = null,
    val onGrowthFailure: GrowthFailureStrategy = GrowthFailureStrategy.Continue,
)

enum class GrowthFailureStrategy {
    Continue,
    Trap,
}
