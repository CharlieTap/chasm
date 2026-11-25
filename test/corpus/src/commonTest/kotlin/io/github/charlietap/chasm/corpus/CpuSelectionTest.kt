package io.github.charlietap.chasm.corpus

import kotlin.test.Test
import kotlin.test.assertEquals

class CpuSelectionTest {

    @Test
    fun `a cpu list expands its ranges and single cpus in order`() {
        assertEquals(listOf(0, 1, 2, 3, 5, 7, 8), cpuIndices("0-3,5,7-8"))
    }

    @Test
    fun `a cpu list of one cpu is that cpu`() {
        assertEquals(listOf(7), cpuIndices("7"))
    }

    @Test
    fun `the fastest cpu is the one with the highest maximum frequency`() {

        val frequencies = mapOf(0 to 1_959_000L, 1 to 1_959_000L, 4 to 2_592_000L, 7 to 2_900_000L, 9 to 3_110_000L)

        assertEquals(9, fastestCpu(listOf(0, 1, 4, 7, 9)) { cpu -> frequencies.getValue(cpu) })
    }

    @Test
    fun `the lowest index wins between cpus of the same maximum frequency`() {

        val frequencies = mapOf(0 to 1_959_000L, 4 to 2_592_000L, 7 to 2_900_000L, 8 to 2_900_000L)

        assertEquals(7, fastestCpu(listOf(8, 0, 7, 4)) { cpu -> frequencies.getValue(cpu) })
    }
}
