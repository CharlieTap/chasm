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
    private val analysis = analyzeModule(module, code, ModuleIndexSpaces(module))

    @Test
    fun `the fixture lowers to every instruction that allocates`() {
        val allocating = code.functions.flatMap { function -> function.instructions.asList() }
            .filter { instruction -> instruction.allocatesGuestObject() }
            .mapNotNullTo(HashSet()) { instruction -> instruction::class.simpleName }

        assertEquals(
            setOf(
                "StructNewS",
                "StructNewDefaultS",
                "ArrayNewIi",
                "ArrayNewIs",
                "ArrayNewSi",
                "ArrayNewSs",
                "ArrayNewDefaultI",
                "ArrayNewDefaultS",
                "ArrayNewFixedS",
                "ArrayNewDataIi",
                "ArrayNewDataIs",
                "ArrayNewDataSi",
                "ArrayNewDataSs",
                "ArrayNewElementIi",
                "ArrayNewElementIs",
                "ArrayNewElementSi",
                "ArrayNewElementSs",
                "Throw",
            ),
            allocating,
        )
    }

    @Test
    fun `every allocation collects, whatever its operands`() {
        val allocations = listOf(
            "struct_new",
            "struct_new_default",
            "array_new_ii",
            "array_new_is",
            "array_new_si",
            "array_new_ss",
            "array_new_default_i",
            "array_new_default_s",
            "array_new_fixed",
            "array_new_data_ii",
            "array_new_data_is",
            "array_new_data_si",
            "array_new_data_ss",
            "array_new_elem_ii",
            "array_new_elem_is",
            "array_new_elem_si",
            "array_new_elem_ss",
            "throws",
        )

        allocations.forEach { name -> assertEffects(name, collects = true, grows = false) }
    }

    @Test
    fun `reading writing copying converting and rethrowing never collect`() {
        listOf("get", "touch_arrays", "touch_references", "rethrows").forEach { name ->
            assertEffects(name, collects = false, grows = false)
        }
    }

    @Test
    fun `a call to an import or an unknown function collects and grows memory`() {
        listOf("host", "calls_import", "calls_indirect", "calls_indirect_dynamic", "calls_ref", "tail_indirect", "tail_ref")
            .forEach { name -> assertEffects(name, collects = true, grows = true) }
    }

    @Test
    fun `a call to a function that collects collects, directly, through a chain, a cycle or a tail call`() {
        listOf("via_alloc", "calls_chain", "cycle_a", "cycle_b", "tail_alloc", "calls_tail_alloc").forEach { name ->
            assertEffects(name, collects = true, grows = false)
        }
    }

    @Test
    fun `calls among functions that never collect, recursive ones included, never collect`() {
        listOf("calls_get", "even", "odd").forEach { name -> assertEffects(name, collects = false, grows = false) }
    }

    @Test
    fun `memory growth reaches callers directly and through tail calls`() {
        listOf("grows", "calls_grows", "tail_grows").forEach { name -> assertEffects(name, collects = false, grows = true) }
    }

    private fun assertEffects(
        name: String,
        collects: Boolean,
        grows: Boolean,
    ) {
        val index = index(name)
        assertEquals(collects, analysis.mayCollect[index], "$name collects")
        assertEquals(grows, analysis.mayGrowMemory[index], "$name grows memory")
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
