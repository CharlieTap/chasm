package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.localIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalTeeExecutorImplTest {

    @Test
    fun `can execute a local tee instruction`() {

        val stack = stack()

        val local = NumberValue.I32(0)

        val frame = frame(
            state = Stack.Entry.ActivationFrame.State(
                locals = mutableListOf(local),
                module = moduleInstance(),
            ),
        )

        stack.push(frame)

        val instruction = VariableInstruction.LocalTee(localIndex())

        val expected = NumberValue.I32(117)
        stack.push(value(expected))

        val actual = LocalTeeExecutorImpl(
            instruction = instruction,
            stack = stack,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(value(expected), stack.popValueOrNull())
        assertEquals(expected, frame.state.locals[0])
    }
}
