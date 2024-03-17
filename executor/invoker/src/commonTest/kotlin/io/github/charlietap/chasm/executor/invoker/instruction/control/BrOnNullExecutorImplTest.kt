package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instruction.labelIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BrOnNullExecutorImplTest {

    @Test
    fun `can execute a bronnull and break when null is on top of the stack`() {

        val stack = stack()
        val instruction = ControlInstruction.BrOnNull(labelIndex())

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(value(referenceValue))

        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(instruction.labelIndex, _labelIndex)

            Ok(Unit)
        }

        val actual = BrOnNullExecutorImpl(stack, instruction, breakExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.valuesDepth())
    }

    @Test
    fun `can execute a bronnull and do nothing when a non null reference is on top of the stack`() {

        val stack = stack()
        val instruction = ControlInstruction.BrOnNull(labelIndex())

        val referenceValue = ReferenceValue.FunctionAddress(functionAddress())
        stack.push(value(referenceValue))

        val breakExecutor: BreakExecutor = { _, _ ->
            fail("BreakExecutor should not be called in this scenario")
        }

        val actual = BrOnNullExecutorImpl(stack, instruction, breakExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValue()?.value)
    }
}
