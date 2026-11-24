package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.compiled.gc.managedEntryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CompiledStackTraceTest {

    @Test
    fun `the shrinker rules keep the class whose frames separate managed runs`() {
        assertEquals(MANAGED_ENTRY_CLASS, managedEntryClass.java.name)
    }
}
