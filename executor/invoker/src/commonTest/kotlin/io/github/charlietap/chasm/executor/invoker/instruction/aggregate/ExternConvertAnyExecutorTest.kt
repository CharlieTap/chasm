package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.structReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ExternConvertAnyExecutorTest {

    @Test
    fun `can execute the ExternConvertAny instruction when the value is ref null and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val nullRef = nullReferenceValue()

        stack.pushValue(nullRef)

        val actual = ExternConvertAnyExecutor(context, AggregateInstruction.ExternConvertAny)

        assertEquals(Unit, actual)
        assertEquals(ReferenceValue.Null(AbstractHeapType.Extern), stack.popValueOrNull())
    }

    @Test
    fun `can execute the ExternConvertAny instruction when the value is a non null ref and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)

        val nonNullReferenceValues = setOf(
            i31ReferenceValue(),
            structReferenceValue(),
            arrayReferenceValue(),
            functionReferenceValue(),
            hostReferenceValue(),
        )

        nonNullReferenceValues.forEach { referenceValue ->
            stack.pushValue(referenceValue)

            val actual = ExternConvertAnyExecutor(context, AggregateInstruction.ExternConvertAny)

            assertEquals(Unit, actual)
            assertEquals(ReferenceValue.Extern(referenceValue), stack.popValueOrNull())
        }
    }
}
