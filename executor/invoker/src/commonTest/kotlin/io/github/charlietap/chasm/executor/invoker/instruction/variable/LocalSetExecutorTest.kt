package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
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

        val stack = stack()
        val context = executionContext(stack)

        val local = i32(0)

        val frame = frame(
            locals = mutableListOf(local),
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

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(0, stack.valuesDepth())
        assertEquals(expected, frame.locals[0])
    }
}
