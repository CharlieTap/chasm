package io.github.charlietap.chasm.runtime.store

import kotlin.test.Test
import kotlin.test.assertNull

class StackDefaultsTest {

    @Test
    fun `the default stack is the platform's`() {
        assertNull(Store().stackBytes)
    }
}
