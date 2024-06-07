package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefI31ExecutorImplTest {

    @Test
    fun `can execute the RefI31 instruction and return a correct result`() {

        val stack = stack()
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.pushValue(i32(i32))

        val i31Wrapper: (Int) -> ReferenceValue.I31 = { input ->
            assertEquals(i32, input)
            i31
        }

        val actual = RefI31ExecutorImpl(stack, i31Wrapper)

        assertEquals(Ok(Unit), actual)
        assertEquals(i31, stack.popValueOrNull()?.value)
    }
}
