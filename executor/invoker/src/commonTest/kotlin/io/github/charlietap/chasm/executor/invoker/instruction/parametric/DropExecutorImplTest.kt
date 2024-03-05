package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class DropExecutorImplTest {

    @Test
    fun `can execute the drop instruction`() {

        val stack = stack()

        stack.push(value(NumberValue.I32(117)))

        val actual = DropExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.valuesDepth())
    }
}
