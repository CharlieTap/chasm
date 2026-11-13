package io.github.charlietap.chasm.compiler.backend.dex.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.SlotReadVisitor
import io.github.charlietap.chasm.compiler.backend.analysis.SlotWriteVisitor
import io.github.charlietap.chasm.compiler.backend.analysis.fallsThrough
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.frameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.hasBit
import io.github.charlietap.chasm.compiler.backend.analysis.instructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets
import io.github.charlietap.chasm.compiler.backend.analysis.setBit
import io.github.charlietap.chasm.compiler.backend.analysis.visitSlotReads
import io.github.charlietap.chasm.compiler.backend.analysis.visitSlotWrites
import io.github.charlietap.chasm.compiler.backend.dex.decodeModule
import io.github.charlietap.chasm.compiler.backend.dex.fixture
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlockLivenessTest {

    private val livenessModule = decodeModule(fixture("liveness"))
    private val livenessDeclarations = ModuleIndexSpaces(livenessModule)
    private val livenessCode = ModuleCompiler(livenessModule).getOrThrow { error -> IllegalStateException(error.toString()) }

    @Test
    fun `every range of every fixture function matches a per instruction fixpoint`() {

        var ranges = 0
        for (name in FIXTURES) {

            val module = decodeModule(fixture(name))
            val declarations = ModuleIndexSpaces(module)
            val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }

            for (function in code.functions) {

                val slots = frameFacts(function, declarations).frameSlots
                val graph = livenessGraph(instructionTable(function, declarations), function, slots)
                val size = function.instructions.size
                val everything = LongArray(graph.words).also { bits -> for (slot in 0 until slots) bits.setBit(slot) }

                for ((start, end) in ranges(size)) {
                    for (exit in listOf(LongArray(graph.words), everything)) {

                        val expected = fixpoint(function, declarations, slots, start, end, exit)
                        val liveness = graph.solve(start, end, exit)

                        for (position in start until end) {
                            val actual = LongArray(graph.words).also { bits -> liveness.addLiveIn(position, bits) }
                            assertContentEquals(expected[position - start], actual, "$name f${function.index.index} [$start,$end) at $position")
                            assertEquals(expected[position - start].sumOf { word -> word.countOneBits() }, liveness.liveCount(position))
                        }
                        assertEquals(0, liveness.liveCount(end))

                        ranges++
                    }
                }
            }
        }

        assertTrue(ranges > 0)
    }

    @Test
    fun `aggregate and reference operands stay live until their last read`() {
        assertEquals(
            listOf(listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), listOf(0, 1), listOf(5, 6), listOf(0)),
            liveIn(function("aggregates")),
        )
    }

    @Test
    fun `a slot written before it is read is not live on entry`() {
        assertEquals(listOf(listOf(1), listOf(1), listOf(0, 1), listOf(0)), liveIn(function("overwritten")))
    }

    @Test
    fun `a slot read after a loop is live throughout it`() {
        assertEquals(listOf(listOf(0, 1), listOf(0, 1), listOf(0, 1), listOf(0, 1), listOf(1)), liveIn(function("loop")))
    }

    @Test
    fun `an instruction whose reads are unknown keeps every slot live`() {

        val graph = LivenessGraph(
            slots = 4,
            blocks = basicBlocks(booleanArrayOf(true, false, false), 3),
            successorStart = intArrayOf(0, 1),
            successors = intArrayOf(-1),
            predecessorStart = intArrayOf(0, 0),
            predecessors = IntArray(0),
            modes = ByteArray(3),
            writeOffsets = intArrayOf(0, 1, 1, 1),
            writeSlots = intArrayOf(2),
            readOffsets = intArrayOf(0, 0, 0, 0),
            readSlots = IntArray(0),
            unknownReads = booleanArrayOf(false, true, false),
        )
        val liveness = graph.solve(0, 3, LongArray(graph.words))

        assertEquals(listOf(listOf(0, 1, 3), listOf(0, 1, 2, 3), emptyList()), (0 until 3).map { position -> liveness.liveAt(position, 4) })
        assertEquals(listOf(3, 4, 0), (0 until 3).map(liveness::liveCount))
    }

    private fun liveIn(function: CirFunction): List<List<Int>> {

        val slots = frameFacts(function, livenessDeclarations).frameSlots
        val graph = livenessGraph(instructionTable(function, livenessDeclarations), function, slots)
        val liveness = graph.solve(0, function.instructions.size, LongArray(graph.words))

        return function.instructions.indices.map { position -> liveness.liveAt(position, slots) }
    }

    private fun BlockLiveness.liveAt(
        position: Int,
        slots: Int,
    ): List<Int> {

        val bits = LongArray((slots + 63) / 64).also { bits -> addLiveIn(position, bits) }

        return (0 until slots).filter(bits::hasBit)
    }

    private fun function(name: String): CirFunction {

        val export = livenessModule.exports.single { export -> export.name.name == name }
        val index = (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()

        return livenessCode.functions.single { function -> function.index.index == index }
    }

    private fun ranges(size: Int): List<Pair<Int, Int>> {
        return if (size <= ALL_RANGES) {
            (0 until size).flatMap { start -> (start + 1..size).map { end -> start to end } }
        } else {
            intArrayOf(1, 7, 40, 150).flatMap { width -> (0 until size step width).map { start -> start to minOf(size, start + width) } } + (0 to size)
        }
    }

    private fun fixpoint(
        function: CirFunction,
        declarations: ModuleIndexSpaces,
        slots: Int,
        start: Int,
        end: Int,
        exit: LongArray,
    ): Array<LongArray> {

        val words = exit.size
        val count = end - start
        val reads = Array(count) { LongArray(words) }
        val kills = Array(count) { LongArray(words) }
        for (index in 0 until count) {

            val instruction = function.instructions[start + index]
            val read = reads[index]
            val modelled = visitSlotReads(instruction, declarations, Bits(read))
            if (!modelled) {
                read.fill(0L)
                for (slot in 0 until slots) read.setBit(slot)
            }

            val conditional = instruction is AdminInstruction.JumpIfCopyI || instruction is AdminInstruction.JumpIfCopyS
            if (!conditional) {
                visitSlotWrites(instruction, function, declarations, Bits(kills[index]))
            }
        }

        val liveIn = Array(count) { LongArray(words) }
        var changed = true
        while (changed) {
            changed = false
            for (index in count - 1 downTo 0) {

                val instruction = function.instructions[start + index]
                val out = LongArray(words)
                val successor = { target: Int ->
                    val source = if (target in start until end) liveIn[target - start] else exit
                    for (word in 0 until words) out[word] = out[word] or source[word]
                }

                if (instruction.fallsThrough()) {
                    successor(start + index + 1)
                }
                instruction.jumpTargets().forEach(successor)
                if (instruction is AdminInstruction.JumpTableS) {
                    successor(end)
                }

                for (word in 0 until words) {
                    val next = reads[index][word] or (out[word] and kills[index][word].inv())
                    if (next != liveIn[index][word]) {
                        liveIn[index][word] = next
                        changed = true
                    }
                }
            }
        }

        return liveIn
    }

    private class Bits(
        private val bits: LongArray,
    ) : SlotReadVisitor, SlotWriteVisitor {
        override fun read(slot: Int) {
            set(slot)
        }

        override fun value(
            slot: Int,
            type: ValueType,
        ) {
            set(slot)
        }

        override fun opaque(
            slot: Int,
            reference: Boolean,
        ) {
            set(slot)
        }

        override fun copy(
            source: Operand,
            destination: Int,
        ) {
            set(destination)
        }

        private fun set(slot: Int) {
            bits.setBit(slot)
        }
    }
}

private val FIXTURES = listOf(
    "bench",
    "bench_gc",
    "calls",
    "control",
    "eh",
    "gc",
    "liveness",
    "memory",
    "routes",
    "routes_eh",
    "scalar",
    "slot_kinds",
    "tail",
    "wide",
)

private const val ALL_RANGES = 80
