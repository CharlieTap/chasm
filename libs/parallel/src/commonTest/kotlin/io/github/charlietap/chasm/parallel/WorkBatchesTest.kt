package io.github.charlietap.chasm.parallel

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkBatchesTest {

    @Test
    fun `items are batched in order until each batch holds enough work`() {
        val work = intArrayOf(6, 6, 1, 9, 1)

        val batches = workBatches(work.size, batchWork = 8) { item -> work[item] }

        assertEquals(listOf(0 to 2, 2 to 4, 4 to 5), batches.ranges())
    }

    @Test
    fun `an item larger than a batch has one to itself`() {
        val work = intArrayOf(100, 1, 1)

        val batches = workBatches(work.size, batchWork = 8) { item -> work[item] }

        assertEquals(listOf(0 to 1, 1 to 3), batches.ranges())
    }

    @Test
    fun `no items make no batches`() {
        val batches = workBatches(0, batchWork = 8) { 1 }

        assertEquals(0, batches.count)
    }

    @Test
    fun `a single batch holds every item`() {
        assertEquals(listOf(0 to 5), singleBatch(5).ranges())
    }

    private fun WorkBatches.ranges(): List<Pair<Int, Int>> = List(count) { batch -> start(batch) to end(batch) }
}
