package io.github.charlietap.chasm.compiler.backend.dex

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.compiler.backend.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindFlow
import io.github.charlietap.chasm.compiler.backend.dex.analysis.functionFacts
import io.github.charlietap.chasm.compiler.backend.dex.lower.RegisterHomes
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlotKindFlowTest {

    private val module = decodeModule(fixture("slot_kinds"))
    private val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
    private val lowering = ModuleLowering(module, code, DexOptions())

    @Test
    fun `a slot written as an int on both paths reads as an int where they join, though it held a float before`() {

        val flow = flow(MERGE)

        assertEquals(SlotKindFlow.FLOAT, flow.written(1, 3))
        assertEquals(SlotKindFlow.INT, flow.kindBefore(MERGE, 6, 3))
        assertTrue(flow.tracks(3))
        assertEquals(homes(SlotKindFlow.INT, SlotKindFlow.FLOAT), flow.homeMask(3))
    }

    @Test
    fun `a select of a float and a narrow immediate writes a float over the int condition`() {

        val flow = flow(SELECT)

        assertEquals(SlotKindFlow.FLOAT, flow.written(1, 0))
        assertEquals(SlotKindFlow.FLOAT, flow.kindBefore(SELECT, 2, 0))
        assertEquals(homes(SlotKindFlow.INT, SlotKindFlow.FLOAT), flow.homeMask(0))
    }

    @Test
    fun `each destination of a sequence copy takes the kind of its own source`() {

        val flow = flow(COPIES)

        assertEquals(SlotKindFlow.INT, flow.written(1, 3))
        assertEquals(SlotKindFlow.DOUBLE, flow.written(1, 4))
        assertEquals(homes(SlotKindFlow.INT), flow.homeMask(3))
        assertEquals(homes(SlotKindFlow.DOUBLE), flow.homeMask(4))
    }

    @Test
    fun `a conditional copy changes the destination kind only on the branch`() {

        val flow = flow(CONDITIONAL)

        assertEquals(SlotKindFlow.LONG, flow.written(3, 4))
        assertEquals(SlotKindFlow.FLOAT, flow.kindBefore(CONDITIONAL, 4, 4))
        assertEquals(SlotKindFlow.LONG, flow.kindBefore(CONDITIONAL, 5, 4))
        assertEquals(homes(SlotKindFlow.LONG, SlotKindFlow.FLOAT), flow.homeMask(4))
    }

    @Test
    fun `a slot holding a long on one path and a double on the other is pinned to a long home`() {

        val flow = flow(CONFLICT)

        assertEquals(SlotKindFlow.CONFLICT_WIDE, flow.kindBefore(CONFLICT, 6, 4))
        assertEquals(SlotKindFlow.LONG, flow.pin(4))
        assertEquals(SlotKindFlow.LONG, flow.pin(1))
        assertEquals(SlotKindFlow.UNDEFINED, flow.pin(2))
        assertEquals(homes(SlotKindFlow.LONG), flow.homeMask(4))
        assertEquals(homes(SlotKindFlow.LONG), flow.homeMask(1))
    }

    @Test
    fun `a self tail call writes its parameters with their declared kinds`() {

        val flow = flow(RESTART)

        assertEquals(SlotKindFlow.INT, flow.written(3, 0))
        assertEquals(SlotKindFlow.FLOAT, flow.written(3, 1))
    }

    @Test
    fun `a caught payload reaches its handler as a long`() {

        val flow = flow(PAYLOAD)

        assertTrue(flow.tracks(2))
        assertEquals(SlotKindFlow.FLOAT, flow.kindBefore(PAYLOAD, 3, 2))
        assertEquals(SlotKindFlow.LONG, flow.kindBefore(PAYLOAD, 7, 2))
        assertEquals(homes(SlotKindFlow.LONG, SlotKindFlow.FLOAT), flow.homeMask(2))
    }

    @Test
    fun `pinning a slot for one lowering attempt leaves the analysed kinds untouched and stops reuse`() {

        val flow = flow(CONDITIONAL)
        val initialised = BooleanArray(frameSlots(CONDITIONAL)) { true }
        val attempt = RegisterHomes(flow, initialised)
        assertTrue(attempt.reusableFor(initialised))
        assertFalse(attempt.reusableFor(BooleanArray(initialised.size)))

        attempt.pinConflict(4)

        assertEquals(SlotKindFlow.LONG, attempt.pin(4))
        assertEquals(homes(SlotKindFlow.LONG), attempt.mask(4))
        assertEquals(homes(SlotKindFlow.FLOAT), attempt.mask(2))
        assertEquals(SlotKindFlow.UNDEFINED, flow.pin(4))
        assertEquals(homes(SlotKindFlow.LONG, SlotKindFlow.FLOAT), flow.homeMask(4))
        assertFalse(attempt.reusableFor(initialised))
    }

    @Test
    fun `pinning a narrow slot keeps its narrow home and the analysis pins`() {

        val attempt = RegisterHomes(flow(CONFLICT), BooleanArray(frameSlots(CONFLICT)) { true })

        attempt.pinConflict(0)

        assertEquals(SlotKindFlow.INT, attempt.pin(0))
        assertEquals(homes(SlotKindFlow.INT), attempt.mask(0))
        assertEquals(SlotKindFlow.LONG, attempt.pin(4))
    }

    @Test
    fun `each link of a descending chain of backward jumps merges the kinds its neighbours write`() {

        val chain = chain()
        val flow = analyze(chain)
        val slot = chain.frame.frameSlots - 1
        val state = ByteArray(chain.frame.frameSlots)

        val before = chain.instructions.indices.map { position ->

            flow.initialKinds(state)
            flow.before(position, state)

            state[slot]
        }

        assertEquals(
            listOf(
                SlotKindFlow.ANY_NARROW,
                SlotKindFlow.FLOAT,
                SlotKindFlow.LONG,
                SlotKindFlow.CONFLICT,
                SlotKindFlow.FLOAT,
                SlotKindFlow.CONFLICT,
                SlotKindFlow.INT,
                SlotKindFlow.CONFLICT_NARROW,
                SlotKindFlow.LONG,
                SlotKindFlow.CONFLICT,
                SlotKindFlow.FLOAT,
                SlotKindFlow.CONFLICT,
                SlotKindFlow.INT,
                SlotKindFlow.CONFLICT_NARROW,
                SlotKindFlow.LONG,
                SlotKindFlow.LONG,
                SlotKindFlow.FLOAT,
                SlotKindFlow.CONFLICT,
            ),
            before,
        )
        assertEquals(
            listOf(
                SlotKindFlow.LONG,
                SlotKindFlow.FLOAT,
                SlotKindFlow.INT,
                SlotKindFlow.LONG,
                SlotKindFlow.FLOAT,
                SlotKindFlow.INT,
                SlotKindFlow.LONG,
                SlotKindFlow.FLOAT,
            ),
            (1..LINKS).map { link -> flow.written(2 * link - 1, slot) },
        )
        assertTrue(flow.tracks(slot))
        assertEquals(homes(SlotKindFlow.LONG, SlotKindFlow.INT, SlotKindFlow.FLOAT), flow.homeMask(slot))
        assertEquals(SlotKindFlow.UNDEFINED, flow.pin(slot))
    }

    private fun chain(): CirFunction {

        val base = code.functions[CONDITIONAL]
        val slot = base.frame.frameSlots
        val exit = 2 * LINKS + 1

        val instructions = ArrayList<Instruction>()
        instructions += AdminInstruction.Jump(InstructionIndex(2 * LINKS - 1))
        for (link in 1..LINKS) {
            instructions += AdminInstruction.CopySlot(sourceSlot = link % 3, destinationSlot = slot)
            instructions += AdminInstruction.JumpIfS(operandSlot = 0, target = InstructionIndex(if (link == 1) exit else 2 * link - 3))
        }
        instructions += base.instructions.last()

        return base.copy(frame = base.frame.copy(frameSlots = slot + 1), instructions = instructions.toTypedArray())
    }

    private fun frameSlots(function: Int): Int {
        return functionFacts(lowering, code.functions[function]).frame.frameSlots
    }

    private fun flow(function: Int): SlotKindFlow {
        return analyze(code.functions[function])
    }

    private fun analyze(function: CirFunction): SlotKindFlow {

        val facts = functionFacts(lowering, function)

        return DataflowSlotKindAnalyzer().analyze(function, facts.table, facts.frame, BooleanArray(facts.frame.frameSlots) { true })
    }

    private fun SlotKindFlow.kindBefore(
        function: Int,
        position: Int,
        slot: Int,
    ): Byte {

        val state = ByteArray(frameSlots(function))
        initialKinds(state)
        before(position, state)

        return state[slot]
    }

    private fun homes(vararg kinds: Byte): Int {
        return kinds.fold(0) { mask, kind -> mask or SlotKindFlow.homeBit(kind) }
    }
}

private const val MERGE = 0

private const val SELECT = 1

private const val COPIES = 2

private const val CONDITIONAL = 3

private const val CONFLICT = 4

private const val RESTART = 5

private const val PAYLOAD = 6

private const val LINKS = 8
