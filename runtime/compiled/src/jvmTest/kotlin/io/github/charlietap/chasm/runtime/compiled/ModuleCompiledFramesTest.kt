package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames.Companion.NOT_COMPILED
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleCompiledFramesTest {

    @Test
    fun `a frame belongs to the module whose entry class its class is nested in`() {

        val first = frames("a.First")
        val second = frames("a.Second")

        assertEquals(1, first.function("a.First\$Code0", "f1"))
        assertEquals(3, first.function("a.First\$Chunks3\$0", "f3\$c1"))
        assertEquals(NOT_COMPILED, second.function("a.First\$Code0", "f1"))
        assertEquals(NOT_COMPILED, first.function("a.FirstX\$Code0", "f1"))
    }

    private fun frames(entryClass: String): ModuleCompiledFrames {
        return ModuleCompiledFrames(
            classPrefix = "$entryClass\$",
            chunkStarts = { null },
            originalLine = { _, _, line -> line },
            entryClass = Any::class,
        )
    }
}
