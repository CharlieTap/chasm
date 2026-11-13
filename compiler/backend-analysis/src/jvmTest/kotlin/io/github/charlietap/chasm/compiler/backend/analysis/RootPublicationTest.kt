package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RootPublicationTest {

    private val module = module("root_publication")
    private val code: CirModule = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
    private val declarations = ModuleIndexSpaces(module)
    private val analysis = analyzeModule(module, code, declarations)

    @Test
    fun `a write followed by a collection is published and one after the last is not`() {
        val function = function("trailing")
        val call = call(function)
        assertCopies(function, call - 1)
        assertCopies(function, call + 1)

        val publication = publication(function)

        assertEquals(true, publication.requiresPublicationAfter(call - 1))
        assertEquals(false, publication.requiresPublicationAfter(call + 1))
    }

    @Test
    fun `a write after a collection in a loop is published for the next iteration`() {
        val function = function("loop")
        val call = call(function)
        assertCopies(function, call + 1)

        val publication = publication(function)

        assertEquals(true, publication.requiresPublicationAfter(call + 1))
    }

    @Test
    fun `parameters are published only when a collection follows the entry`() {
        assertEquals(true, publication(function("trailing")).afterEntry)
        assertEquals(false, publication(function("reads")).afterEntry)
    }

    @Test
    fun `the root array is fetched again after a call only when a publication follows before the next fetch`() {

        val trailing = function("trailing")
        val between = function("between")
        val calls = between.instructions.indices.filter { position -> between.instructions[position] is ControlInstruction.Call }
        val refreshes = references(between).refreshes
        val trailingRefreshes = references(trailing).refreshes

        assertEquals(true, trailingRefreshes.afterEntry)
        assertEquals(false, trailingRefreshes.refreshRequiredAfter(call(trailing)))
        assertEquals(2, calls.size)
        assertEquals(true, refreshes.refreshRequiredAfter(calls[0]))
        assertEquals(true, refreshes.arrayCurrentAt(calls[0] + 1))
        assertEquals(false, refreshes.refreshRequiredAfter(calls[1]))
        assertEquals(false, refreshes.arrayCurrentAt(calls[1] + 1))
    }

    private fun publication(function: CirFunction): RootPublication {
        val table = instructionTable(function, declarations)
        return analyzeRootPublication(flowGraph(table, function), function, analysis.mayCollect, needs = null)
    }

    private fun references(function: CirFunction): References {
        val facts = frameFacts(function, declarations)
        return analyzeReferences(instructionTable(function, declarations), function, facts.type, facts.frameSlots, analysis)
    }

    private fun assertCopies(
        function: CirFunction,
        position: Int,
    ) {
        val instruction = function.instructions[position]
        assertTrue(instruction is AdminInstruction.CopySlot || instruction is AdminInstruction.CopySlotSequence, "$instruction")
    }

    private fun call(
        function: CirFunction,
    ): Int = function.instructions.indices.single { position -> function.instructions[position] is ControlInstruction.Call }

    private fun function(
        name: String,
    ): CirFunction {
        val export = module.exports.single { export -> export.name.name == name }
        val index = (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()
        return code.functions.single { function -> function.index.index == index }
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
