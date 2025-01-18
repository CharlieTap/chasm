package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.localTeeRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalTeeExecutorTest {

    @Test
    fun `can execute a local tee instruction`() {

        val local = i32(0)
        val stack = stack(
            values = listOf(local),
        )
        val context = executionContext(stack)

        val frame = frame(
            instance = moduleInstance(),
        )

        stack.push(frame)

        val instruction = localTeeRuntimeInstruction(0)

        val expected = i32(117)
        stack.push(expected)

        val actual = LocalTeeExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(2, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
        assertEquals(expected, stack.local(0))
    }
}
