package io.github.charlietap.chasm.benchmark

internal actual object PlatformBenchmarkControl {

    actual fun enter(mode: BenchmarkMode): PolicyApplication =
        PolicyApplication(
            mode = mode,
            isApplied = false,
            message = "Benchmark placement control is unavailable on Android",
            isSupported = false,
        )

    actual fun topology(): BenchmarkTopology =
        BenchmarkTopology(
            logicalCpuCount = Runtime.getRuntime().availableProcessors(),
            fastestCpuCount = null,
            fastestClassName = null,
            fastestCpuIds = emptySet(),
            source = CpuTopologySource.UNAVAILABLE,
        )

    actual fun currentCpu(): Int? = null

    actual fun sleepMillis(durationMillis: Long) {
        if (durationMillis == 0L) {
            Thread.yield()
        } else {
            Thread.sleep(durationMillis)
        }
    }
}
