package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.StoreLimits
import kotlin.test.Test
import kotlin.test.assertNull

class StoreLimitsDefaultsTest {

    @Test
    fun `the stack limit defaults to the platform ceiling on the jvm`() {
        assertNull(StoreLimits().stackBytes)
    }
}
