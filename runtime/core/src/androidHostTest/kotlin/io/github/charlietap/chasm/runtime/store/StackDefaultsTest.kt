package io.github.charlietap.chasm.runtime.store

import kotlin.test.Test
import kotlin.test.assertEquals

class StackDefaultsTest {

    @Test
    fun `the default stack is the platform's`() {
        assertEquals(16L * 1024 * 1024, Store().stackBytes)
    }
}
