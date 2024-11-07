package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.instruction.refEqInstruction
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.fixture.value.structReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class RefEqExecutorTest {

    @Test
    fun `can execute the RefEq instruction and return false when reference types match`() {

        val stack = stack()
        val context = executionContext(stack)
        val reference = structReferenceValue()
        stack.pushValue(reference)
        stack.pushValue(reference)

        val actual = RefEqExecutor(
            context = context,
            instruction = refEqInstruction(),
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(1), stack.popValueOrNull()?.value)
    }

    @Test
    fun `can execute the RefEq instruction and return false when reference types do not match`() {

        val stack = stack()
        val context = executionContext(stack)
        stack.pushValue(structReferenceValue())
        stack.pushValue(structReferenceValue())

        val actual = RefEqExecutor(
            context = context,
            instruction = refEqInstruction(),
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(0), stack.popValueOrNull()?.value)
    }
}
