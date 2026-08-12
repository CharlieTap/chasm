package io.github.charlietap.chasm.benchmark

internal expect object PlatformBenchmarkControl {
    fun enter(mode: BenchmarkMode): PolicyApplication

    fun topology(): BenchmarkTopology

    fun currentCpu(): Int?

    fun sleepMillis(durationMillis: Long)
}
