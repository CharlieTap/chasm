package io.github.charlietap.chasm.parallel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EarliestFailureTest {

    @Test
    fun `there is no error until one is reported`() {
        val failures = EarliestFailure<String>()

        assertNull(failures.error)
        assertFalse(failures.isBeyond(0))
    }

    @Test
    fun `the failure at the lowest position is kept whatever order they arrive in`() {
        val failures = EarliestFailure<String>()

        failures.report(7, "seventh")
        failures.report(3, "third")
        failures.report(5, "fifth")

        assertEquals("third", failures.error)
    }

    @Test
    fun `only positions after the failure are beyond it`() {
        val failures = EarliestFailure<String>()

        failures.report(3, "third")

        assertFalse(failures.isBeyond(2))
        assertFalse(failures.isBeyond(3))
        assertTrue(failures.isBeyond(4))
    }
}
