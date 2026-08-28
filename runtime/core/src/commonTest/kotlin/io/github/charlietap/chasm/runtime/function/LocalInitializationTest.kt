package io.github.charlietap.chasm.runtime.function

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LocalInitializationTest {

    @Test
    fun selectsSpecializedZeroInitialization() {
        assertSame(LocalInitialization.None, classifyLocalInitialization(longArrayOf()))
        assertSame(LocalInitialization.Zero1, classifyLocalInitialization(longArrayOf(0)))
        assertSame(LocalInitialization.Zero2, classifyLocalInitialization(longArrayOf(0, 0)))
        assertSame(LocalInitialization.Zero3, classifyLocalInitialization(longArrayOf(0, 0, 0)))
        assertSame(LocalInitialization.Zero4, classifyLocalInitialization(longArrayOf(0, 0, 0, 0)))
        assertEquals(
            LocalInitialization.ZeroRange(5),
            classifyLocalInitialization(longArrayOf(0, 0, 0, 0, 0)),
        )
    }

    @Test
    fun retainsNonZeroLocalConstants() {
        val values = longArrayOf(0, 7, -1)

        val initialization = assertIs<LocalInitialization.ConstantStores>(classifyLocalInitialization(values))

        assertTrue(values.contentEquals(initialization.values))
    }
}
