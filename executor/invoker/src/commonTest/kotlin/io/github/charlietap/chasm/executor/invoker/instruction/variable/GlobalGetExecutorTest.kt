package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalGetExecutorTest {

    @Test
    fun `can execute a global get instruction`() {

        val executionValue = i32(117)
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
            instance = moduleInstance,
        )

        stack.push(frame)

        val instruction = VariableInstruction.GlobalGet(globalInstance)

        val expected = executionValue

        val actual = GlobalGetExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }
}
