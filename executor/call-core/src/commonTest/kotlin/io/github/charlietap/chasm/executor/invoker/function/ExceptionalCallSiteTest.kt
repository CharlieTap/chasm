package io.github.charlietap.chasm.executor.invoker.function

import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionalCallSiteTest {

    @Test
    fun `ordinary return addresses identify the preceding call instruction`() {
        for (resultCount in listOf(0, 1, 2)) {
            assertEquals(0, exceptionalCallSiteIp(1, resultCount))
            assertEquals(27, exceptionalCallSiteIp(28, resultCount))
        }
    }

    @Test
    fun `encoded single result returns already identify the call instruction`() {
        assertEquals(0, exceptionalCallSiteIp(resultCallSiteIp(1), 1))
        assertEquals(27, exceptionalCallSiteIp(resultCallSiteIp(28), 1))
    }

    @Test
    fun `other result counts preserve high bits of ordinary return addresses`() {
        val returnIp = (1 shl 30) or 28
        for (resultCount in listOf(0, 2)) {
            assertEquals(returnIp - 1, exceptionalCallSiteIp(returnIp, resultCount))
        }
    }
}
