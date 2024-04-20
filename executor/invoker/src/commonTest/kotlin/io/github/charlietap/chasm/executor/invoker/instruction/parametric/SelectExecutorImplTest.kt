package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectExecutorImplTest {

    @Test
    fun `can execute the select instruction and choose value 1`() {

        val stack = stack()

        val value1 = value(NumberValue.I32(117))
        val value2 = value(NumberValue.I32(118))

        stack.push(value1)
        stack.push(value2)
        stack.push(value(NumberValue.I32(1)))

        val actual = SelectExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(value1, stack.popValueOrNull())
    }

    @Test
    fun `can execute the select instruction and choose value 2`() {

        val stack = stack()

        val value1 = value(NumberValue.I32(117))
        val value2 = value(NumberValue.I32(118))

        stack.push(value1)
        stack.push(value2)
        stack.push(value(NumberValue.I32(0)))

        val actual = SelectExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(value2, stack.popValueOrNull())
    }
}
