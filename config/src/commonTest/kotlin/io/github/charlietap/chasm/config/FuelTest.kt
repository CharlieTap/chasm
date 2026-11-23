package io.github.charlietap.chasm.config

import kotlin.test.Test
import kotlin.test.assertFailsWith

class FuelTest {

    @Test
    fun `negative fuel is refused`() {
        assertFailsWith<IllegalArgumentException> { Fuel(-1) }
    }
}
