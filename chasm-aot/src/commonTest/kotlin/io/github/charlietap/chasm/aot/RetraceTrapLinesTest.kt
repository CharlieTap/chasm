package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.TrapLines
import io.github.charlietap.chasm.aot.internal.readTrapLines
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalChasmApi::class)
class RetraceTrapLinesTest {

    @Test
    fun `lines R8 renumbered map back to the lines of the compiled method that kept them`() {
        val lines = retrace(
            """
            com.example.traps.Code0 -> com.example.traps.Code0:
            # {"id":"sourceFile","fileName":"module.wasm"}
                12:19:java.lang.Void io.github.charlietap.chasm.runtime.compiled.CompiledRuntime.unreachable():34:34 -> f1
                12:19:void f1(com.example.traps.Binding,io.github.charlietap.chasm.runtime.execution.ExecutionContext,int):2 -> f1
                  # {"id":"com.android.tools.r8.residualsignature","signature":"(La8;Lxg;I)V"}
                20:23:void f1(com.example.traps.Binding,io.github.charlietap.chasm.runtime.execution.ExecutionContext,int):5:8 -> f1
                1:4:int f2(int):7:7 -> f2
                5:6:int f2(int) -> f2
            com.example.traps.Chunks3${'$'}0 -> com.example.traps.Chunks3${'$'}0:
            # {"id":"sourceFile","fileName":"module.wasm"}
                3:9:void f3${'$'}c1(com.example.traps.Binding):4:4 -> f3${'$'}c1
            """,
        ).getValue("com/example/traps")

        assertEquals(2, lines.original("Code0", "f1", 12))
        assertEquals(2, lines.original("Code0", "f1", 19))
        assertEquals(7, lines.original("Code0", "f1", 22))
        assertEquals(7, lines.original("Code0", "f2", 3))
        assertEquals(6, lines.original("Code0", "f2", 6))
        assertEquals(4, lines.original("Chunks3\$0", "f3\$c1", 5))
        assertEquals(-1, lines.original("Code0", "f1", 30))
        assertEquals(-1, lines.original("Code0", "f9", 1))
    }

    @Test
    fun `outlined lines and frames of other methods are left unknown`() {
        val lines = retrace(
            """
            com.example.traps.Code0 -> com.example.traps.Code0:
            # {"id":"sourceFile","fileName":"module.wasm"}
                1:3:void f1(int):2:2 -> f1
                4:4:void f1(int):3:3 -> f1
                  # {"id":"com.android.tools.r8.outlineCallsite","positions":{"1":5},"outline":"La;b()V"}
                5:6:void helper(int):9:9 -> f1
            """,
        ).getValue("com/example/traps")

        assertEquals(2, lines.original("Code0", "f1", 2))
        assertEquals(-1, lines.original("Code0", "f1", 4))
        assertEquals(-1, lines.original("Code0", "f1", 5))
    }

    @Test
    fun `a package whose lines all stay unknown still gets a table`() {
        val lines = retrace(
            """
            com.example.traps.Code0 -> com.example.traps.Code0:
            # {"id":"sourceFile","fileName":"module.wasm"}
                4:4:void f1(int):3:3 -> f1
                  # {"id":"com.android.tools.r8.outlineCallsite","positions":{"1":5},"outline":"La;b()V"}
            """,
        ).getValue("com/example/traps")

        assertEquals(-1, lines.original("Code0", "f1", 4))
    }

    @Test
    fun `classes Chasm did not compile with trap diagnostics get no table`() {
        val tables = retrace(
            """
            com.example.plain.Code0 -> com.example.plain.Code0:
                1:3:void f1(int):2:2 -> f1
            com.example.app.Main -> a:
            # {"id":"sourceFile","fileName":"module.wasm"}
                1:3:void f1(int):2:2 -> f1
            """,
        )

        assertEquals(emptySet(), tables.keys)
    }

    private fun retrace(
        mapping: String,
    ): Map<String, TrapLines> = retraceTrapLines(mapping.trimIndent().reader()).entries.associate { (path, bytes) ->
        path.substringBeforeLast('/') to checkNotNull(readTrapLines(bytes.inputStream())) { "unreadable table $path" }
    }
}
