package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class InstructionTableTest {

    private val module = module("instruction_table")
    private val code: CirModule = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
    private val declarations = ModuleIndexSpaces(module)

    @Test
    fun `memory accesses record their position, memory, address slot and end offset`() {

        val accesses = table("accesses").accesses

        assertContentEquals(intArrayOf(1, 2, 3), accesses.positions)
        assertContentEquals(intArrayOf(0, 1, 0), accesses.memories)
        assertContentEquals(intArrayOf(0, MemoryAccessTable.CONSTANT_ADDRESS, 0), accesses.addresses)
        assertContentEquals(longArrayOf(12, 4, 4), accesses.endOffsets)
        assertEquals(listOf(-1, 0, 1, 2, -1), (0..4).map(accesses::accessIndexAt))
    }

    @Test
    fun `a range uses a memory only when an access to it lies inside`() {

        val accesses = table("accesses").accesses

        assertEquals(true, accesses.usesMemory(0, 5, 1))
        assertEquals(false, accesses.usesMemory(3, 5, 1))
        assertEquals(false, accesses.usesMemory(2, 3, 0))
        assertEquals(true, accesses.usesMemory(3, 4, 0))
    }

    @Test
    fun `jumps record their targets at their position`() {

        val jumps = table("jumps").jumps
        val expected = listOf<List<Int>>(emptyList(), emptyList(), listOf(6), emptyList(), emptyList(), listOf(1), emptyList())

        assertEquals(expected, (0..6).map { position -> jumps.targetsAt(position) })
        assertEquals(2, jumps.count)
    }

    @Test
    fun `copies record their source slots and typed writes record their kind`() {

        val writes = table("copies").writes

        assertEquals(SlotWriteTable.SEQUENTIAL, writes.modes[1])
        assertEquals(listOf(3, 4), writes.slotsAt(1))
        assertEquals(listOf(0, 1), writes.sourcesAt(1))
        assertEquals(listOf(WriteKinds.COPIED, WriteKinds.COPIED), writes.kindsAt(1))
        assertEquals(listOf(6), writes.slotsAt(2))
        assertEquals(listOf(SlotWriteTable.NOT_A_COPY), writes.sourcesAt(2))
        assertEquals(listOf(WriteKinds.LONG), writes.kindsAt(2))
        assertEquals(listOf(0), writes.slotsAt(3))
        assertEquals(4, writes.count)
    }

    @Test
    fun `a helper result is narrow unless it is a reference`() {

        val writes = table("helpers").writes

        assertEquals(listOf(2), writes.slotsAt(1))
        assertEquals(listOf(WriteKinds.LONG), writes.kindsAt(1))
        assertEquals(true, writes.references[writes.start[1]])
        assertEquals(listOf(0), writes.slotsAt(2))
        assertEquals(listOf(WriteKinds.INT), writes.kindsAt(2))
        assertEquals(false, writes.references[writes.start[2]])
    }

    @Test
    fun `a self tail call outside a try region restarts the function and copies its arguments to the parameters`() {

        val function = function("self")
        val table = instructionTable(function, declarations)

        assertEquals(listOf(3), function.instructions.indices.filter(table::restartsFunction))
        assertEquals(listOf(0), table.writes.slotsAt(3))
        assertEquals(listOf(2), table.writes.sourcesAt(3))
    }

    @Test
    fun `a self tail call inside a try region does not restart the function`() {

        val function = function("guarded")
        val table = instructionTable(function, declarations)

        assertEquals(emptyList(), function.instructions.indices.filter(table::restartsFunction))
        assertEquals(listOf(-1, 0, -1), function.instructions.indices.map(table::innermostRegion))
    }

    private fun JumpTable.targetsAt(position: Int): List<Int> {
        return (start[position] until end(position)).map { edge -> targets[edge] }
    }

    private fun SlotWriteTable.slotsAt(position: Int): List<Int> {
        return (start[position] until end(position)).map { index -> slots[index] }
    }

    private fun SlotWriteTable.sourcesAt(position: Int): List<Int> {
        return (start[position] until end(position)).map { index -> sources[index] }
    }

    private fun SlotWriteTable.kindsAt(position: Int): List<Byte> {
        return (start[position] until end(position)).map { index -> kinds[index] }
    }

    private fun table(name: String): InstructionTable {
        return instructionTable(function(name), declarations)
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
}
