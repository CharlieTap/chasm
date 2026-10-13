package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import kotlin.test.Test
import kotlin.test.assertEquals

class selectDecodingStrategyTest {

    private val policy = ParallelismPolicy(
        minimumWork = 64 * 1024L,
        workPerWorker = 32 * 1024L,
        maximumWorkers = 6,
    )

    @Test
    fun `keeps small modules serial`() {
        val workers = selectDecodingWorkers(
            bodies = bodyRanges(512, 512),
            mode = DecodingMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(1, workers)
    }

    @Test
    fun `uses a worker for every share of body bytes`() {
        val workers = selectDecodingWorkers(
            bodies = bodyRanges(32 * 1024, 32 * 1024, 32 * 1024, 32 * 1024),
            mode = DecodingMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(4, workers)
    }

    @Test
    fun `never uses more workers than the platform is tuned for`() {
        val workers = selectDecodingWorkers(
            bodies = bodyRanges(*IntArray(16) { 32 * 1024 }),
            mode = DecodingMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(6, workers)
    }

    @Test
    fun `one large body leaves only the rest to share`() {
        val workers = selectDecodingWorkers(
            bodies = bodyRanges(300 * 1024, *IntArray(100) { 1024 }),
            mode = DecodingMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(4, workers)
    }

    @Test
    fun `a forced strategy ignores how little there is to decode`() {
        val bodies = bodyRanges(1024, 1024)

        assertEquals(2, selectDecodingWorkers(bodies, DecodingMode.PARALLEL, policy, availableProcessors = 4))
        assertEquals(1, selectDecodingWorkers(bodies, DecodingMode.PARALLEL, policy, availableProcessors = 1))
        assertEquals(1, selectDecodingWorkers(bodies, DecodingMode.SERIAL, policy, availableProcessors = 4))
    }

    @Test
    fun `batches bodies in the order they appear`() {
        val batches = batchBodies(bodyRanges(6000, 6000, 100, 9000, 100), workerCount = 2)

        assertEquals(listOf(0 to 2, 2 to 4, 4 to 5), List(batches.count) { batches.start(it) to batches.end(it) })
    }

    @Test
    fun `a single worker has every body in one batch`() {
        val batches = batchBodies(bodyRanges(6000, 6000, 100, 9000, 100), workerCount = 1)

        assertEquals(listOf(0 to 5), List(batches.count) { batches.start(it) to batches.end(it) })
    }

    private fun bodyRanges(vararg sizes: Int): CodeBodyRanges = CodeBodyRanges(
        starts = IntArray(sizes.size),
        ends = IntArray(sizes.size),
        sizes = sizes,
    )
}
