package io.github.charlietap.chasm.parallel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WorkCursorTest {

    @Test
    fun `each piece of work is claimed once and in order`() {
        val cursor = WorkCursor(3)

        assertEquals(listOf(0, 1, 2), List(3) { cursor.claim() })
        assertEquals(NOTHING_TO_CLAIM, cursor.claim())
        assertEquals(NOTHING_TO_CLAIM, cursor.claim())
    }

    @Test
    fun `there is never anything to claim from no work`() {
        val cursor = WorkCursor(0)

        assertEquals(NOTHING_TO_CLAIM, cursor.claim())
        assertEquals(NOTHING_TO_CLAIM, cursor.claim())
    }

    @Test
    fun `claiming runs the action on everything left`() {
        val cursor = WorkCursor(4)
        cursor.claim()
        val claimed = mutableListOf<Int>()

        ParallelTaskScope {}.claimEach(cursor) { claimed += it }

        assertEquals(listOf(1, 2, 3), claimed)
    }

    @Test
    fun `claiming stops when the task is no longer active`() {
        val cursor = WorkCursor(4)
        var claims = 0
        val scope = ParallelTaskScope { if (claims == 2) throw IllegalStateException("cancelled") }

        assertFailsWith<IllegalStateException> {
            scope.claimEach(cursor) { claims++ }
        }
        assertEquals(2, cursor.claim())
    }
}
