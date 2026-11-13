package io.github.charlietap.chasm.compiler.backend.dex.analysis

import kotlin.test.Test
import kotlin.test.assertEquals

class BlockWorklistTest {

    @Test
    fun `an empty worklist has no next block`() {
        assertEquals(-1, BlockWorklist(0).next())
    }

    @Test
    fun `blocks come out in index order and a block added twice comes out once`() {

        val worklist = BlockWorklist(10)
        worklist.add(7)
        worklist.add(2)
        worklist.add(7)

        assertEquals(listOf(2, 7), drain(worklist))
    }

    @Test
    fun `a block added after the current one comes out in the same pass and one at or before it waits for the next`() {

        val worklist = BlockWorklist(10)
        worklist.add(4)

        assertEquals(4, worklist.next())

        worklist.add(4)
        worklist.add(1)
        worklist.add(8)
        worklist.add(6)

        assertEquals(listOf(6, 8, 1, 4), drain(worklist))
    }

    @Test
    fun `blocks either side of each word boundary come out in order`() {
        for (blocks in listOf(64, 65, 4096, 4097)) {

            val worklist = BlockWorklist(blocks)
            val added = listOf(blocks - 1, 0, 63, 64, 4095, 4096).filter { block -> block < blocks }.distinct()
            added.forEach(worklist::add)

            assertEquals(added.sorted(), drain(worklist), "$blocks blocks")
        }
    }

    @Test
    fun `a pass over many words wraps to the lowest block waiting for the next`() {

        val worklist = BlockWorklist(4097)
        worklist.add(4096)

        assertEquals(4096, worklist.next())

        worklist.add(4000)
        worklist.add(1)
        worklist.add(4096)

        assertEquals(listOf(1, 4000, 4096), drain(worklist))
    }

    private fun drain(worklist: BlockWorklist): List<Int> {

        val blocks = ArrayList<Int>()
        var block = worklist.next()
        while (block >= 0) {
            blocks += block
            block = worklist.next()
        }

        return blocks
    }
}
