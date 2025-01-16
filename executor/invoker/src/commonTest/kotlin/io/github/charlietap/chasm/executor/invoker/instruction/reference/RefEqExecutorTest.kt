package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.refEqRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.structReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class RefEqExecutorTest {

    @Test
    fun `can execute the RefEq instruction and return false when reference types match`() {

        val stack = stack()
        val context = executionContext(stack)
        val reference = structReferenceValue()
        stack.push(reference)
        stack.push(reference)

        val actual = RefEqExecutor(
            context = context,
            instruction = refEqRuntimeInstruction(),
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(1), stack.popValue())
    }

    @Test
    fun `can execute the RefEq instruction and return false when reference types do not match`() {

        val stack = stack()
        val context = executionContext(stack)
        stack.push(structReferenceValue())
        stack.push(structReferenceValue())

        val actual = RefEqExecutor(
            context = context,
            instruction = refEqRuntimeInstruction(),
        )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(0), stack.popValue())
    }
}
