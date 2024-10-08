package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.globalInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalGetExecutorTest {

    @Test
    fun `can execute a global get instruction`() {

        val executionValue = i32(117)
        val globalIndex = globalIndex()
        val globalInstance = globalInstance(
            value = executionValue,
        )
        val globalAddress = globalAddress()
        val moduleInstance = moduleInstance(
            globalAddresses = mutableListOf(globalAddress),
        )
        val store = store(
            globals = mutableListOf(
                globalInstance,
            ),
        )
        val stack = stack()
        val context = executionContext(stack, store)

        val frame = frame(
            state = Stack.Entry.ActivationFrame.State(
                locals = mutableListOf(),
                module = moduleInstance,
            ),
        )

        stack.push(frame)

        val instruction = VariableInstruction.GlobalGet(globalIndex)

        val expected = Stack.Entry.Value(executionValue)

        val actual = GlobalGetExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
