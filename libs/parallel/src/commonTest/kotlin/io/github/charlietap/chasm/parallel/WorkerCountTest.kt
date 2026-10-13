package io.github.charlietap.chasm.parallel

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkerCountTest {

    private val policy = ParallelismPolicy(minimumWork = 1_000, workPerWorker = 250)

    @Test
    fun `work below the minimum is not shared`() {
        assertEquals(1, selectWorkerCount(999, 10, 100, policy, availableProcessors = 8))
    }

    @Test
    fun `every share of work gets a worker`() {
        assertEquals(4, selectWorkerCount(1_000, 10, 100, policy, availableProcessors = 8))
    }

    @Test
    fun `there are never more workers than processors`() {
        assertEquals(8, selectWorkerCount(100_000, 10, 100, policy, availableProcessors = 8))
    }

    @Test
    fun `there are never more workers than items`() {
        assertEquals(3, selectWorkerCount(100_000, 40_000, 3, policy, availableProcessors = 8))
    }

    @Test
    fun `there are never more workers than the policy allows`() {
        val capped = ParallelismPolicy(minimumWork = 1_000, workPerWorker = 250, maximumWorkers = 6)

        assertEquals(6, selectWorkerCount(100_000, 10, 100, capped, availableProcessors = 8))
    }

    @Test
    fun `one large item leaves only the rest of the work to share`() {
        assertEquals(2, selectWorkerCount(1_300, 1_000, 100, policy, availableProcessors = 8))
        assertEquals(1, selectWorkerCount(1_200, 1_000, 100, policy, availableProcessors = 8))
    }

    @Test
    fun `fewer workers than the policy requires is not worth sharing`() {
        val reluctant = ParallelismPolicy(minimumWork = 1_000, workPerWorker = 250, minimumWorkers = 4)

        assertEquals(1, selectWorkerCount(1_600, 1_000, 100, reluctant, availableProcessors = 8))
        assertEquals(4, selectWorkerCount(1_000, 10, 100, reluctant, availableProcessors = 8))
    }

    @Test
    fun `no work is never shared`() {
        val eager = ParallelismPolicy(minimumWork = 0, workPerWorker = 1)

        assertEquals(1, selectWorkerCount(0, 0, 0, eager, availableProcessors = 8))
    }

    @Test
    fun `more work than can be counted is shared between every processor`() {
        val eager = ParallelismPolicy(minimumWork = 0, workPerWorker = 1)

        assertEquals(8, selectWorkerCount(Long.MAX_VALUE, 0, 100, eager, availableProcessors = 8))
    }

    @Test
    fun `one processor is never shared`() {
        assertEquals(1, selectWorkerCount(100_000, 10, 100, policy, availableProcessors = 1))
    }
}
