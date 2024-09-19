package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefEqExecutorTest {

    @Test
    fun `can execute the RefEq instruction and return true when reference types match`() {

        val stack = stack()
        val functionAddress = functionAddress()
        stack.pushValue(ReferenceValue.Function(functionAddress))
        stack.pushValue(ReferenceValue.Function(functionAddress))

        val actual = RefEqExecutor(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(1), stack.popValueOrNull()?.value)
    }

    @Test
    fun `can execute the RefEq instruction and return false when reference types do not match`() {

        val stack = stack()
        val functionAddress1 = functionAddress(1)
        val functionAddress2 = functionAddress(2)
        stack.pushValue(ReferenceValue.Function(functionAddress1))
        stack.pushValue(ReferenceValue.Function(functionAddress2))

        val actual = RefEqExecutor(stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(0), stack.popValueOrNull()?.value)
    }
}
