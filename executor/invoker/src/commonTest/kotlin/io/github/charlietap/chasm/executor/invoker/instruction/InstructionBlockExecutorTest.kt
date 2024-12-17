package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockExecutorTest {

    @Test
    fun `can execute an instruction block return a result`() {

        val stack = stack()
        val label = label()

        val instructions = arrayOf(
            dispatchableInstruction(),
            dispatchableInstruction(),
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
            handler = null,
            handlerDispatcher = handlerDispatcher,
            labelDispatcher = labelDispatcher,
        )

        val expectedInstructions = arrayOf(
            labelInstruction,
        ) + instructions

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.labelsDepth())
        assertEquals(label, stack.popLabelOrNull())
        assertEquals(expectedInstructions.toList(), stack.instructions())
    }
}
