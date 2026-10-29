package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.host.ModuleIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InspectModuleTest {

    @Test
    fun `describes a module's exports`() {
        val info = inspectModule(module("declarations"), ModuleConfig(decodeNameSection = true))
            .expect("declarations module should decode")

        val started = info.exports.single { export -> export.name == "started" }
        assertIs<ModuleIndex.FunctionIndex>(started.index)
        assertTrue(info.exports.any { export -> export.name == "struct" })
        assertTrue(info.imports.isEmpty())
    }

    @Test
    fun `describes a module that would not validate`() {
        val info = inspectModule(module("invalid")).expect("invalid module should still decode")

        assertEquals(emptyList(), info.imports)
        assertEquals(emptyList(), info.exports)
    }

    @Test
    fun `fails on a module exporting a function it does not have`() {
        assertIs<ChasmResult.Error<*>>(inspectModule(module("missing_export")))
    }

    @Test
    fun `fails on a module importing a function of a type it does not have`() {
        assertIs<ChasmResult.Error<*>>(inspectModule(module("missing_type")))
    }

    @Test
    fun `fails on bytes that are not a wasm module`() {
        assertIs<ChasmResult.Error<*>>(inspectModule(byteArrayOf(1, 2, 3)))
    }

    private fun module(
        name: String,
    ): ByteArray =
        checkNotNull(InspectModuleTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }
            .use { stream -> stream.readBytes() }
}
