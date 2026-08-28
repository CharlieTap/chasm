package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompilerInstructionTagTranslatorTest {

    private val translator = CompilerInstructionTagTranslator()

    @Test
    fun `translates instruction variants without recording operands`() {
        assertEquals(
            "numeric.i32.add.si",
            translator.translate(NumericInstruction.I32AddSi(1, 2, 3)),
        )
        assertEquals(
            "numeric.i32.const",
            translator.translate(NumericInstruction.I32ConstS(1, 2)),
        )
        assertEquals(
            "parametric.select.sis",
            translator.translate(ParametricInstruction.SelectSis(1, 2L, 3, 4)),
        )
        assertEquals(
            "variable.global_set.s",
            translator.translateVariant(VariableInstruction.GlobalSetS::class.java),
        )
    }

    @Test
    fun `translates fused condition shape and polarity`() {
        val instruction = AdminInstruction.JumpIfConditionMismatch(
            condition = NumericCondition.I32LtS(
                left = FusedOperand.FrameSlot(1),
                right = FusedOperand.I32Const(2),
            ),
            targetIp = 3,
        )

        assertEquals(
            "admin.jump_condition.i32.lt_s.si.mismatch",
            translator.translate(instruction),
        )
    }

    @Test
    fun `translates every generated instruction variant`() {
        variantFamilies.forEach { family ->
            val variants = family.declaredClasses.filter(family::isAssignableFrom)
            assertTrue(variants.isNotEmpty(), "${family.name} has no concrete variants")

            val tags = variants.map(translator::translateVariant)
            assertEquals(
                expected = variants.size,
                actual = tags.toSet().size,
                message = "${family.name} contains variants with duplicate tags",
            )
        }
    }
}

private val variantFamilies = listOf(
    ParametricInstruction::class.java,
    VariableInstruction::class.java,
    NumericInstruction::class.java,
    MemoryInstruction::class.java,
    TableInstruction::class.java,
    AggregateSuperInstruction::class.java,
)
