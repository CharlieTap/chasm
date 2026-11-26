package io.github.charlietap.chasm.jvm

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShrinkerRulesTest {

    @Test
    fun `a module with GC types writes no GC rule`() = runTest {

        val output = ClassOutput.memory()
        val config = CompileConfig("test.rules.gc.Module", runtime = RuntimeConfig(stacktraces = true))

        val info = compile(module("managed_objects"), output, config).expect("compiles")
        val rules = info.shrinkerRules.lines().filter { line -> line.isNotBlank() }

        assertFalse(info.shrinkerRules.contains(".gc.storage"))
        assertEquals(3, rules.size)
        assertTrue(rules.last().contains("io.github.charlietap.chasm.runtime.compiled.gc.ManagedEntry"))
    }

    private fun module(name: String): ByteArray {
        return checkNotNull(ShrinkerRulesTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }.use { stream ->
            stream.readBytes()
        }
    }
}
