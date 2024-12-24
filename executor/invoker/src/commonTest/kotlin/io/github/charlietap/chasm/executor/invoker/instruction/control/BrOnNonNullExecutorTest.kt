package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BrOnNonNullExecutorTest {

    @Test
    fun `can execute a bronnonnull and break when a non null reference is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)
        val instruction = ControlInstruction.BrOnNonNull(labelIndex())

        val referenceValue = ReferenceValue.Function(functionAddress())
        stack.push(referenceValue)

        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(instruction.labelIndex, _labelIndex)

            Ok(Unit)
        }

        val actual = BrOnNonNullExecutor(context, instruction, breakExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValueOrNull())
    }

    @Test
    fun `can execute a bronnull and do nothing when a null reference is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)
        val instruction = ControlInstruction.BrOnNonNull(labelIndex())

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(referenceValue)

        val breakExecutor: BreakExecutor = { _, _ ->
            fail("BreakExecutor should not be called in this scenario")
        }

        val actual = BrOnNonNullExecutor(context, instruction, breakExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.valuesDepth())
    }
}
