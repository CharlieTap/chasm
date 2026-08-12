package io.github.charlietap.chasm.benchmark

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

public object BenchmarkStabilizer {

    /** Applies a scheduling policy to the calling thread only. */
    public fun enter(
        mode: BenchmarkMode = BenchmarkMode.PREFER_FASTEST,
        topology: BenchmarkTopology = topology(),
    ): PolicyApplication = if (topology.isPlacementSupported) {
        PlatformBenchmarkControl.enter(mode)
    } else {
        PolicyApplication(
            mode = mode,
            isApplied = false,
            message = "Fastest-core placement is unavailable; continuing without it",
            isSupported = false,
        )
    }

    /** Returns the performance topology visible to the current process. */
    public fun topology(): BenchmarkTopology = PlatformBenchmarkControl.topology()

    /** Samples the logical CPU executing the calling thread. */
    public fun sample(topology: BenchmarkTopology = topology()): CpuSample {
        if (!topology.isPlacementSupported) return CpuSample(null, null)

        val cpuId = PlatformBenchmarkControl.currentCpu()
        return CpuSample(cpuId, cpuId?.let(topology::isFastest) ?: false)
    }

    /**
     * Waits briefly for a requested policy to move the calling thread to the fastest class.
     *
     * Call this outside the timed region. The last sample is returned on timeout.
     */
    public fun awaitFastestCore(
        timeout: Duration = 100.milliseconds,
        topology: BenchmarkTopology = topology(),
    ): CpuSample {
        if (!topology.isPlacementSupported) return CpuSample(null, null)

        val start = TimeSource.Monotonic.markNow()
        var sample = sample(topology)

        while (sample.isFastest == false && start.elapsedNow() < timeout) {
            PlatformBenchmarkControl.sleepMillis(1)
            sample = sample(topology)
        }

        return sample
    }

    /** Samples the end placement and validates both trial endpoints. */
    public fun finishTrial(
        start: CpuSample,
        topology: BenchmarkTopology = topology(),
    ): TrialPlacement = TrialPlacement(
        start = start,
        end = sample(topology),
        isSupported = topology.isPlacementSupported,
    )

    /**
     * Blocks the calling thread between realtime trials so Darwin's failsafe does not
     * treat it as continuously monopolising a CPU. Never call this inside a timed region.
     */
    public fun checkpoint(
        duration: Duration = 5.milliseconds,
        topology: BenchmarkTopology = topology(),
    ) {
        require(!duration.isNegative()) { "Checkpoint duration must not be negative" }
        if (!topology.isPlacementSupported) return

        PlatformBenchmarkControl.sleepMillis(duration.inWholeMilliseconds)
    }
}
