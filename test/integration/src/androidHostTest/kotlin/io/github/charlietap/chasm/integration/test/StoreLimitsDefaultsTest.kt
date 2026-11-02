package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.StoreLimits
import kotlin.test.Test
import kotlin.test.assertEquals

class StoreLimitsDefaultsTest {

    @Test
    fun `the stack limit defaults to 16 MiB on android`() {
        assertEquals(16L * 1024 * 1024, StoreLimits().stackBytes)
    }
}
