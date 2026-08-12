package io.github.charlietap.chasm.benchmark

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BenchmarkPlacementTest {

    @Test
    fun validatesFastestEndpoints() {
        val placement = TrialPlacement(
            start = CpuSample(12, true),
            end = CpuSample(15, true),
        )

        assertTrue(placement.isValid)
    }

    @Test
    fun rejectsSlowEndpoint() {
        val placement = TrialPlacement(
            start = CpuSample(12, true),
            end = CpuSample(3, false),
        )

        assertFalse(placement.isValid)
    }

    @Test
    fun reportsUnknownWhenTopologyIsUnavailable() {
        val topology = BenchmarkTopology(8, null, null, emptySet(), CpuTopologySource.UNAVAILABLE)

        assertFalse(topology.isPlacementSupported)
        assertNull(topology.isFastest(0))
    }

    @Test
    fun unavailableTopologyMakesHighLevelApiANoOp() {
        val topology = BenchmarkTopology(8, null, null, emptySet(), CpuTopologySource.UNAVAILABLE)

        val application = BenchmarkStabilizer.enter(topology = topology)
        val start = BenchmarkStabilizer.awaitFastestCore(topology = topology)
        val placement = BenchmarkStabilizer.finishTrial(start, topology)
        BenchmarkStabilizer.checkpoint(topology = topology)

        assertFalse(application.isSupported)
        assertFalse(application.isApplied)
        assertTrue(application.canProceed)
        assertNull(start.cpuId)
        assertNull(start.isFastest)
        assertFalse(placement.isSupported)
        assertTrue(placement.isValid)
    }
}
