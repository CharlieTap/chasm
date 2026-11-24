package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals

class CallEffectsTest {

    private val module = module("call_effects")
    private val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
    private val analysis = analyzeModule(code, ModuleIndexSpaces(module))

    @Test
    fun `allocating, reading and rethrowing never grow memory`() {
        listOf("struct_new", "array_new_ss", "get", "touch_arrays", "touch_references", "rethrows").forEach { name ->
            assertGrows(name, grows = false)
        }
    }

    @Test
    fun `a call to an import or an unknown function grows memory`() {
        listOf("host", "calls_import", "calls_indirect", "calls_indirect_dynamic", "calls_ref", "tail_indirect", "tail_ref")
            .forEach { name -> assertGrows(name, grows = true) }
    }

    @Test
    fun `calls among functions that never grow memory, recursive ones included, never grow memory`() {
        listOf("calls_get", "even", "odd").forEach { name -> assertGrows(name, grows = false) }
    }

    @Test
    fun `memory growth reaches callers directly and through tail calls`() {
        listOf("grows", "calls_grows", "tail_grows").forEach { name -> assertGrows(name, grows = true) }
    }

    private fun assertGrows(
        name: String,
        grows: Boolean,
    ) {
        assertEquals(grows, analysis.mayGrowMemory[index(name)], "$name grows memory")
    }

    private fun index(
        name: String,
    ): Int {
        val export = module.exports.single { export -> export.name.name == name }
        return (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()
    }

    private fun module(
        name: String,
    ): Module {
        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { it.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
    }
}
