package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KotlinSourceGeneratorTest {
    @Test
    fun `numeric intermediates stay in locals until block exit`() {
        val source = KotlinSourceGenerator().generate(
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
        val source = KotlinSourceGenerator(maxBlockInstructions = 2, maxClassInstructions = 3).generate(
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
    fun `unsupported post MVP instructions fail without interpreter fallback`() {
        assertFailsWith<IllegalArgumentException> {
            KotlinSourceGenerator().generate(0, listOf(ControlInstruction.ThrowRefS(0)), intArrayOf(0))
        }
    }
}
