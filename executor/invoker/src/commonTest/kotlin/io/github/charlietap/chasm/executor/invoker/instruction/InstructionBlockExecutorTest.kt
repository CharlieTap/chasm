package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Value
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockExecutorTest {

    @Test
    fun `can execute an instruction block return a result`() {

        val stack = stack()

        val label = label()

        val instructions = listOf(
            dispatchableInstruction(),
            dispatchableInstruction(),
        )
        val params = listOf(
            i32(1),
            i32(2),
        )
        val handlerInstruction = dispatchableInstruction()
        val handlerDispatcher: Dispatcher<ExceptionHandler> = { handler ->
            handlerInstruction
        }

        val labelInstruction = dispatchableInstruction()
        val labelDispatcher: Dispatcher<Stack.Entry.Label> = { label ->
            labelInstruction
        }

        val actual = InstructionBlockExecutor(
            stack = stack,
            label = label,
            instructions = instructions,
            params = params,
            handler = null,
            handlerDispatcher = handlerDispatcher,
            labelDispatcher = labelDispatcher,
        )

        val expectedParams = params.asReversed().map(::Value)
        val expectedInstructions = listOf(
            Instruction(
                labelInstruction,
                Stack.Entry.InstructionTag.LABEL,
            ),
        ) + instructions.asReversed().map(::Instruction)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.labelsDepth())
        assertEquals(label, stack.popLabelOrNull())
        assertEquals(expectedParams, stack.values())
        assertEquals(expectedInstructions, stack.instructions())
    }
}
