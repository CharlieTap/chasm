package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.TableSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompilerInstructionTagTranslatorTest {

    private val translator = CompilerInstructionTagTranslator()

    @Test
    fun `translates instruction variants without recording operands`() {
        assertEquals(
            "numeric.i32.add.si",
            translator.translate(NumericSuperInstruction.I32AddSi(1, 2, 3)),
        )
        assertEquals(
            "numeric.i32.const",
            translator.translate(NumericSuperInstruction.I32ConstS(1, 2)),
        )
        assertEquals(
            "parametric.select.sis",
            translator.translate(ParametricSuperInstruction.SelectSis(1, 2L, 3, 4)),
        )
        assertEquals(
            "variable.local_set.s",
            translator.translate(VariableSuperInstruction.LocalSetS(1, 2)),
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
    ParametricSuperInstruction::class.java,
    NumericSuperInstruction::class.java,
    MemorySuperInstruction::class.java,
    TableSuperInstruction::class.java,
    AggregateSuperInstruction::class.java,
)
