package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RootNeedsTest {

    private val module = module("root_needs")
    private val code: CirModule = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
    private val declarations = ModuleIndexSpaces(module)
    private val analysis = analyzeModule(module, code, declarations)

    @Test
    fun `roots written by one instruction are published only when a collection follows while they are live`() {

        val function = function("roots")
        val references = references(function)
        val needs = needs(function, references)

        assertContentEquals(intArrayOf(0, 1, 3, 4, 5), references.slots)
        assertEquals(listOf(3, 4, 5), copiedAt(function, 1))
        assertEquals(listOf(1 to 3, 2 to 3, 3 to 3), neededAfter(function, references, needs))
        assertEquals(emptyList(), references.slots.indices.filter(needs::neededOnEntry))
        assertEquals(true, references.publication.requiresPublication(1, references.rootIndex[3]))
        assertEquals(false, references.publication.requiresPublication(1, references.rootIndex[4]))
        assertEquals(false, references.publication.requiresPublication(1, references.rootIndex[5]))
    }

    @Test
    fun `a reference read only by a handler is needed across a call that may raise`() {

        val function = function("caught")
        val references = references(function)
        val needs = needs(function, references)

        assertEquals(listOf(2), copiedAt(function, 1))
        assertEquals(listOf(1 to 2), neededAfter(function, references, needs))
        assertEquals(true, references.publication.requiresPublication(1, references.rootIndex[2]))
    }

    @Test
    fun `catch payload slots are references`() {

        val function = function("caught")
        val payload = checkNotNull(function.exceptions).regions.single().catches.single().payloadSlots

        assertContentEquals(intArrayOf(3), payload)
        assertContentEquals(intArrayOf(0, 2, 3), references(function).slots)
    }

    @Test
    fun `references dead at every collection need no publication and the frame does not write through`() {

        val function = function("dead")
        val references = references(function)

        assertEquals(true, needs(function, references).isEmpty())
        assertContentEquals(intArrayOf(0), references.slots)
        assertEquals(false, references.writesThrough)
    }

    @Test
    fun `a reference read by a handler stays needed through deeply nested try regions`() {

        val function = function("caught")
        val exceptions = checkNotNull(function.exceptions)
        val region = exceptions.regions.single()
        val nested = function.copy(
            exceptions = exceptions.copy(
                regions = List(NESTED_REGIONS) { depth ->
                    if (depth == 0) region else region.copy(parent = depth - 1, catches = emptyList())
                },
            ),
        )
        val references = references(function)

        assertEquals(listOf(1 to 2), neededAfter(nested, references, needs(nested, references)))
    }

    @Test
    fun `without root needs a publishing position publishes every root and entry always publishes`() {

        val function = function("roots")
        val table = instructionTable(function, declarations)
        val publication = analyzeRootPublication(flowGraph(table, function), function, analysis.mayCollect, needs = null)

        assertEquals(listOf(true, true, true, true, true), (0 until 5).map { root -> publication.requiresPublication(1, root) })
        assertEquals(listOf(false, false, false, false, false), (0 until 5).map { root -> publication.requiresPublication(4, root) })
        assertEquals(true, publication.requiresPublication(RootPublication.ENTRY, 0))
    }

    @Test
    fun `root needs fall back to conservative publication once the need matrices and flow graph exceed the word budget`() {

        val wordsPerNode = 1024
        val budget = 1 shl 22
        val graphWords = 2
        val largest = budget / (5 * wordsPerNode + graphWords)

        assertNotNull(needs(straightLine(largest), rootCount = wordsPerNode * Long.SIZE_BITS))
        assertNull(needs(straightLine(largest + 1), rootCount = wordsPerNode * Long.SIZE_BITS))
    }

    private fun needs(
        function: CirFunction,
        rootCount: Int,
    ): RootNeeds? {
        val table = instructionTable(function, declarations)
        return analyzeRootNeeds(
            table = table,
            graph = flowGraph(table, function),
            function = function,
            mayCollect = analysis.mayCollect,
            rootIndex = IntArray(function.frame.frameSlots),
            isReference = BooleanArray(function.frame.frameSlots),
            rootCount = rootCount,
        )
    }

    private fun straightLine(size: Int): CirFunction {
        return CirFunction(
            index = FunctionIndex(0),
            type = TypeIndex(0),
            frame = Frame(frameSlots = 1, activationHeaderSlot = 0),
            instructions = Array(size) { AdminInstruction.Checkpoint },
        )
    }

    private fun neededAfter(
        function: CirFunction,
        references: References,
        needs: RootNeeds,
    ): List<Pair<Int, Int>> {
        return function.instructions.indices.flatMap { position ->
            references.slots.filter { slot -> needs.neededAfter(position, references.rootIndex[slot]) }.map { slot -> position to slot }
        }
    }

    private fun copiedAt(
        function: CirFunction,
        position: Int,
    ): List<Int> {
        val writes = instructionTable(function, declarations).writes
        return (writes.start[position] until writes.end(position)).map { index -> writes.slots[index] }
    }

    private fun needs(
        function: CirFunction,
        references: References,
    ): RootNeeds {
        val table = instructionTable(function, declarations)
        return checkNotNull(
            analyzeRootNeeds(
                table = table,
                graph = flowGraph(table, function),
                function = function,
                mayCollect = analysis.mayCollect,
                rootIndex = references.rootIndex,
                isReference = references.isReference,
                rootCount = references.slots.size,
            ),
        )
    }

    private fun references(function: CirFunction): References {
        val facts = frameFacts(function, declarations)
        return analyzeReferences(instructionTable(function, declarations), function, facts.type, facts.frameSlots, analysis)
    }

    private fun function(name: String): CirFunction {
        val export = module.exports.single { export -> export.name.name == name }
        val index = (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()
        return code.functions.single { function -> function.index.index == index }
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }

        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }
        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val NESTED_REGIONS = 1024
    }
}
