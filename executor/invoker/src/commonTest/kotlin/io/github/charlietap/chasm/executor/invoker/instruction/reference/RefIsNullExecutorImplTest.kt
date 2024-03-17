package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class RefIsNullExecutorImplTest {

    @Test
    fun `can execute the refisnull instruction when a null ref is on top of the stack`() {

        val stack = stack()
        val heapType = heapType()

        stack.push(value(ReferenceValue.Null(heapType)))

        val expected = value(NumberValue.I32(1))

        val actual = RefIsNullExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }

    @Test
    fun `can execute the refisnull instruction when a null ref is not on top of the stack`() {

        val stack = stack()

        stack.push(value(ReferenceValue.FunctionAddress(functionAddress())))

        val expected = value(NumberValue.I32(0))

        val actual = RefIsNullExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }
}
