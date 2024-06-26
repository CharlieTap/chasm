package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefIsNullExecutorImplTest {

    @Test
    fun `can execute the refisnull instruction when a null ref is on top of the stack`() {

        val stack = stack()
        val heapType = heapType()

        stack.push(value(ReferenceValue.Null(heapType)))

        val expected = value(i32(1))

        val actual = RefIsNullExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }

    @Test
    fun `can execute the refisnull instruction when a null ref is not on top of the stack`() {

        val stack = stack()

        stack.push(value(ReferenceValue.Function(functionAddress())))

        val expected = value(i32(0))

        val actual = RefIsNullExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
