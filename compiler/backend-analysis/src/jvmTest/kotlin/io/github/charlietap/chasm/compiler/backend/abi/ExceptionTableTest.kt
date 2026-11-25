package io.github.charlietap.chasm.compiler.backend.abi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExceptionTableTest {

    @Test
    fun `tables built separately with equal payload slots are one resource`() {
        val first = table(intArrayOf(3, 4))
        val second = table(intArrayOf(3, 4))

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(setOf<Resource>(first), setOf<Resource>(second))
    }

    @Test
    fun `tables differing in a payload slot are different resources`() {
        assertNotEquals(table(intArrayOf(3, 4)), table(intArrayOf(3, 5)))
    }

    @Test
    fun `catch all differs from catching tag zero`() {
        val catchAll = CatchClause(null, 8, IntArray(0), exceptionReference = false, stackSlots = 3)
        val tagZero = CatchClause(0, 8, IntArray(0), exceptionReference = false, stackSlots = 3)

        assertNotEquals(catchAll, tagZero)
    }

    private fun table(
        payloadSlots: IntArray,
    ) = Resource.ExceptionTable(
        instructionCount = 10,
        interfaceSlotCount = 2,
        resultCount = 1,
        regions = listOf(
            TryRegion(
                start = 1,
                end = 6,
                parent = null,
                catches = listOf(
                    CatchClause(0, 9, payloadSlots, exceptionReference = true, stackSlots = 5),
                    CatchClause(null, 8, IntArray(0), exceptionReference = false, stackSlots = 3),
                ),
            ),
        ),
    )
}
