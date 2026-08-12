package io.github.charlietap.chasm.benchmark

import kotlinx.benchmark.Setup
import kotlinx.benchmark.TearDown

public abstract class StabilizedBenchmark {

    private var topology: BenchmarkTopology? = null
    private var start: CpuSample? = null

    @Setup
    public fun stabilizeBenchmarkThread() {
        val topology = BenchmarkStabilizer.topology()
        this.topology = topology
        if (!topology.isPlacementSupported) return

        val application = BenchmarkStabilizer.enter(BenchmarkMode.PREFER_FASTEST, topology)
        check(application.canProceed) { application.message ?: "Could not request fastest-core placement" }
        if (!application.isSupported) return

        val start = BenchmarkStabilizer.awaitFastestCore(topology = topology)
        check(start.isFastest == true) {
            "Benchmark started on logical CPU ${start.cpuId}, outside the fastest class"
        }
        this.start = start
    }

    @TearDown
    public fun validateBenchmarkThread() {
        val topology = topology ?: return
        val start = start ?: return
        val placement = BenchmarkStabilizer.finishTrial(start, topology)
        check(placement.isValid) {
            "Benchmark placement was invalid: ${placement.start.cpuId} -> ${placement.end.cpuId}"
        }
    }
}
