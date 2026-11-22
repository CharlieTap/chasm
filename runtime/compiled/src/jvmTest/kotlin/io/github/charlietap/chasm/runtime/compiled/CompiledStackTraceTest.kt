package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.executor.invoker.compiled.compiledEntryClass
import io.github.charlietap.chasm.runtime.compiled.gc.managedEntryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CompiledStackTraceTest {

    @Test
    fun `the shrinker rules keep the class whose frames separate compiled runs`() {
        assertEquals(COMPILED_ENTRY_CLASS, compiledEntryClass.java.name)
    }

    @Test
    fun `the shrinker rules keep the class whose frames separate managed runs`() {
        assertEquals(MANAGED_ENTRY_CLASS, managedEntryClass.java.name)
    }

    private companion object {
        const val MANAGED_ENTRY_CLASS = "io.github.charlietap.chasm.runtime.compiled.gc.ManagedEntry"
    }
}
