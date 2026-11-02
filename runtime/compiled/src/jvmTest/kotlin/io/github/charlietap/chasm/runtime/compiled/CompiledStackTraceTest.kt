package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.aot.internal.COMPILED_ENTRY_CLASS
import io.github.charlietap.chasm.executor.invoker.compiled.compiledEntryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CompiledStackTraceTest {

    @Test
    fun `the shrinker rules keep the class whose frames separate compiled runs`() {
        assertEquals(COMPILED_ENTRY_CLASS, compiledEntryClass.java.name)
    }
}
