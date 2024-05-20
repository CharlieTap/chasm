package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefEqExecutorImplTest {

    @Test
    fun `can execute the RefEq instruction and return true when reference types match`() {

        val stack = stack()
        val functionAddress = functionAddress()
        stack.push(ReferenceValue.Function(functionAddress))
        stack.push(ReferenceValue.Function(functionAddress))

        val actual = RefEqExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(1), stack.popValueOrNull()?.value)
    }

    @Test
    fun `can execute the RefEq instruction and return false when reference types do not match`() {

        val stack = stack()
        val functionAddress1 = functionAddress(1)
        val functionAddress2 = functionAddress(2)
        stack.push(ReferenceValue.Function(functionAddress1))
        stack.push(ReferenceValue.Function(functionAddress2))

        val actual = RefEqExecutorImpl(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(0), stack.popValueOrNull()?.value)
    }
}
