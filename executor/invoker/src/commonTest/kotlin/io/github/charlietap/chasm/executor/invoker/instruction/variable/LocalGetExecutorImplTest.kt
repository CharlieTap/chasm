package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.localIndex
import io.github.charlietap.chasm.fixture.stack
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalGetExecutorImplTest {

    @Test
    fun `can execute a local get instruction`() {

        val stack = stack()

        val local = NumberValue.I32(117)

        val frame = frame(
            state = Stack.Entry.ActivationFrame.State(
                locals = mutableListOf(local),
                module = moduleInstance(),
            ),
        )

        stack.push(frame)

        val instruction = VariableInstruction.LocalGet(localIndex())

        val expected = Stack.Entry.Value(local)

        val actual = LocalGetExecutorImpl(
            instruction = instruction,
            stack = stack,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }
}
