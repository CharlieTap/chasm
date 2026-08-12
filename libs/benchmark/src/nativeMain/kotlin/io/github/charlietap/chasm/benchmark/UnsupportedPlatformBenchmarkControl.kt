package io.github.charlietap.chasm.benchmark

import platform.posix.usleep

internal abstract class UnsupportedPlatformBenchmarkControl(
    private val platformName: String,
) {
    fun enter(mode: BenchmarkMode): PolicyApplication =
        PolicyApplication(
            mode = mode,
            isApplied = false,
            message = "Benchmark placement control is unavailable on $platformName",
            isSupported = false,
        )

    fun topology(): BenchmarkTopology =
        BenchmarkTopology(0, null, null, emptySet(), CpuTopologySource.UNAVAILABLE)

    fun currentCpu(): Int? = null

    fun sleepMillis(durationMillis: Long) {
        usleep((durationMillis * 1_000L).toUInt())
    }
}
