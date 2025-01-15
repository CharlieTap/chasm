package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.refIsNullRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class RefIsNullExecutorTest {

    @Test
    fun `can execute the refisnull instruction when a null ref is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)
        val heapType = heapType()

        stack.push(ReferenceValue.Null(heapType))

        val expected = i32(1)

        val actual = RefIsNullExecutor(
            context = context,
            instruction = refIsNullRuntimeInstruction(),
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }

    @Test
    fun `can execute the refisnull instruction when a null ref is not on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)

        stack.push(ReferenceValue.Function(functionAddress()))

        val expected = i32(0)

        val actual = RefIsNullExecutor(
            context = context,
            instruction = refIsNullRuntimeInstruction(),
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }
}
