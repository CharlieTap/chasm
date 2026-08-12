package io.github.charlietap.chasm.benchmark

public enum class CpuTopologySource {
    DEVICE_TREE,
    UNAVAILABLE,
}

public data class BenchmarkTopology(
    public val logicalCpuCount: Int,
    public val fastestCpuCount: Int?,
    public val fastestClassName: String?,
    public val fastestCpuIds: Set<Int>,
    public val source: CpuTopologySource,
) {
    public val isPlacementSupported: Boolean
        get() = fastestCpuIds.isNotEmpty()

    public fun isFastest(cpuId: Int): Boolean? =
        fastestCpuIds.takeIf(Set<Int>::isNotEmpty)?.contains(cpuId)
}
