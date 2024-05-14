package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Value
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.fixture.instruction.moduleInstruction
import io.github.charlietap.chasm.fixture.label
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i32NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockExecutorImplTest {

    @Test
    fun `can execute an instruction block return a result`() {

        val stack = stack()

        val label = label()

        val instructions = listOf(
            moduleInstruction(NumericInstruction.I32GeS),
            moduleInstruction(NumericInstruction.I32GeU),
        )
        val params = listOf(
            i32NumberValue(1),
            i32NumberValue(2),
        )

        val actual = InstructionBlockExecutorImpl(
            stack = stack,
            label = label,
            instructions = instructions,
            params = params,
        )

        val expectedParams = params.asReversed().map(::Value)
        val expectedInstructions = listOf(Instruction(AdminInstruction.Label(label))) + instructions.asReversed().map(::Instruction)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.labelsDepth())
        assertEquals(label, stack.popLabelOrNull())
        assertEquals(expectedParams, stack.values())
        assertEquals(expectedInstructions, stack.instructions())
    }
}
