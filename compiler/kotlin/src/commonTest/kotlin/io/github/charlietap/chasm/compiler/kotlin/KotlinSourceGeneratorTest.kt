package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinSourceGeneratorTest {
    @Test
    fun `numeric intermediates stay in locals until block exit`() {
        val source = KotlinSourceGenerator(tier = KotlinGenerationTier.BLOCK_LOCALS).generate(
            firstIp = 0,
            instructions = listOf(
                NumericInstruction.I32ConstS(5, 0),
                NumericInstruction.I32ConstS(7, 1),
                NumericInstruction.I32AddSs(0, 1, 0),
                NumericInstruction.I32MulSs(0, 1, 2),
            ),
            functionEntryIps = intArrayOf(0),
        )
        val body = source.groups.single().source
        assertEquals(4, source.promotedInstructionCount)
        assertEquals(0, Regex("vstack.getFrameSlot").findAll(body).count())
        assertEquals(3, Regex("vstack.setFrameSlot").findAll(body).count())
        assertTrue(body.indexOf("valueI32Mul") < body.indexOf("vstack.setFrameSlot"))
    }

    @Test
    fun `branch targets and size limits split generated bodies`() {
        val source = KotlinSourceGenerator(maxBlockInstructions = 2, maxClassInstructions = 3, tier = KotlinGenerationTier.BLOCK_LOCALS).generate(
            firstIp = 100,
            instructions = List(6) { NumericInstruction.I32ConstS(it, it) } + AdminInstruction.Jump(101),
            functionEntryIps = intArrayOf(100),
        )
        assertEquals(listOf(0, 1, 3, 5), source.groups.flatMap { it.blocks }.map { it.startOffset })
        assertTrue(source.groups.all { it.blocks.sumOf(KotlinBlock::size) <= 3 })
        assertEquals(6, source.generatedInstructionCount)
        assertEquals(1, source.controlInstructionCount)
    }

    @Test
    fun `exception transfers remain at original IPs and handlers are generated entries`() {
        val source = KotlinSourceGenerator().generate(
            100,
            listOf(
                ControlInstruction.ThrowRefS(0),
                NumericInstruction.I32ConstS(1, 1),
                NumericInstruction.I32ConstS(2, 1),
                AdminInstruction.JumpOnNullS(0, 104),
                NumericInstruction.I32ConstS(3, 1),
            ),
            intArrayOf(100),
            intArrayOf(102),
        )
        assertEquals(listOf(1, 2, 4), source.groups.flatMap { it.blocks }.map { it.startOffset })
        assertEquals(2, source.controlInstructionCount)
    }

    @Test
    fun `outside branches and catches prevent loop interior elimination`() {
        val instructions = listOf(
            AdminInstruction.JumpIfS(2, 103),
            NumericInstruction.I32AddSi(1, 2, 1),
            AdminInstruction.JumpIfS(2, 105),
            NumericInstruction.I32SubSi(0, 1, 0),
            AdminInstruction.JumpIfS(0, 101),
            NumericInstruction.I32AddSi(1, 10, 3),
        )
        val external = KotlinSourceGenerator().generate(100, instructions, intArrayOf(100))
        assertEquals(0, external.structuredLoopCount)
        assertTrue(3 in external.groups.single().entryOffsets)
        val caught = KotlinSourceGenerator().generate(
            100,
            listOf(NumericInstruction.I32ConstS(3, 0)) + instructions.drop(1),
            intArrayOf(100),
            intArrayOf(103),
        )
        assertEquals(0, caught.structuredLoopCount)
        assertTrue(3 in caught.groups.single().entryOffsets)
    }
}
