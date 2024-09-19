package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class DropExecutorTest {

    @Test
    fun `can execute the drop instruction`() {

        val stack = stack()

        stack.push(value(i32(117)))

        val actual = DropExecutor(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.valuesDepth())
    }
}
