package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefI31ExecutorTest {

    @Test
    fun `can execute the RefI31 instruction and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.push(i32(i32))

        val i31Wrapper: (Int) -> ReferenceValue.I31 = { input ->
            assertEquals(i32, input)
            i31
        }

        val actual = RefI31Executor(context, AggregateInstruction.RefI31, i31Wrapper)

        assertEquals(Unit, actual)
        assertEquals(i31, stack.popValue())
    }
}
