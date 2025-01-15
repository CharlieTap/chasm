package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.localGetRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalGetExecutorTest {

    @Test
    fun `can execute a local get instruction`() {

        val stack = stack()
        val context = executionContext(stack)

        val local = i32(117)

        val frame = frame(
            locals = mutableListOf(local),
            instance = moduleInstance(),
        )

        stack.push(frame)

        val instruction = localGetRuntimeInstruction(0)

        val expected = local

        val actual = LocalGetExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
