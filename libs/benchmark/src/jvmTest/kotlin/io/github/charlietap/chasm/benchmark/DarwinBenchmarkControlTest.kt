package io.github.charlietap.chasm.benchmark

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class DarwinBenchmarkControlTest {

    @Test
    fun discoversConsistentFastestClassFromDeviceTree() {
        if (!isMacOs) return

        val topology = BenchmarkStabilizer.topology()
        if (!topology.isPlacementSupported) return

        topology.fastestCpuCount?.let { count -> assertEquals(count, topology.fastestCpuIds.size) }
        assertTrue(topology.fastestCpuIds.all { it in 0 until topology.logicalCpuCount })
        assertEquals(CpuTopologySource.DEVICE_TREE, topology.source)
    }

    @Test
    fun userInteractivePolicyReachesFastestClass() {
        if (!isMacOs) return

        val topology = BenchmarkStabilizer.topology()
        if (!topology.isPlacementSupported) return
        val application = BenchmarkStabilizer.enter(BenchmarkMode.PREFER_FASTEST, topology)
        val sample = BenchmarkStabilizer.awaitFastestCore(500.milliseconds, topology)

        assertTrue(application.isApplied, application.message)
        assertTrue(sample.isFastest == true, "Observed logical CPU ${sample.cpuId}")
    }

    @Test
    fun realtimePolicyReachesFastestClass() {
        if (!isMacOs) return

        val topology = BenchmarkStabilizer.topology()
        if (!topology.isPlacementSupported) return
        val application = BenchmarkStabilizer.enter(BenchmarkMode.SHORT_REALTIME, topology)
        BenchmarkStabilizer.checkpoint(topology = topology)
        val sample = BenchmarkStabilizer.awaitFastestCore(500.milliseconds, topology)

        assertTrue(application.isApplied, application.message)
        assertTrue(sample.isFastest == true, "Observed logical CPU ${sample.cpuId}")
    }

    @Test
    fun linuxJvmIsANoOp() {
        verifyUnsupportedJvm("Linux")
    }

    @Test
    fun windowsJvmIsANoOp() {
        verifyUnsupportedJvm("Windows 11")
    }

    private fun verifyUnsupportedJvm(osName: String) {
        val topology = PlatformBenchmarkControl.topology(osName)
        val application = BenchmarkStabilizer.enter(topology = topology)
        val start = BenchmarkStabilizer.awaitFastestCore(topology = topology)
        val placement = BenchmarkStabilizer.finishTrial(start, topology)

        assertFalse(topology.isPlacementSupported)
        assertFalse(application.isSupported)
        assertNull(start.cpuId)
        assertTrue(placement.isValid)
    }

    private companion object {
        val isMacOs: Boolean = System.getProperty("os.name") == "Mac OS X"
    }
}
