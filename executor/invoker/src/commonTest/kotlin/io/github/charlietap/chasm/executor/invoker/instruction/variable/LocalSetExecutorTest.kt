package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.localSetRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalSetExecutorTest {

    @Test
    fun `can execute a local set instruction`() {

        val local = i32(0)
        val stack = stack(
            values = listOf(local),
        )
        val context = executionContext(stack)

        val frame = frame(
            instance = moduleInstance(),
        )

        stack.push(frame)

        val instruction = localSetRuntimeInstruction(0)

        val expected = i32(117)
        stack.push(expected)

        val actual = LocalSetExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.local(0))
    }
}
