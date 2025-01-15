package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.externReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.referenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class AnyConvertExternExecutorTest {

    @Test
    fun `can execute the AnyConvertExtern instruction when the value is ref null and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val nullRef = nullReferenceValue()

        stack.pushValue(nullRef)

        val actual = AnyConvertExternExecutor(context, AggregateInstruction.AnyConvertExtern)

        assertEquals(Unit, actual)
        assertEquals(ReferenceValue.Null(AbstractHeapType.Any), stack.popValue())
    }

    @Test
    fun `can execute the AnyConvertExtern instruction when the value is extern ref and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val referenceValue = referenceValue()
        val externReferenceValue = externReferenceValue(referenceValue)

        stack.pushValue(externReferenceValue)

        val actual = AnyConvertExternExecutor(context, AggregateInstruction.AnyConvertExtern)

        assertEquals(Unit, actual)
        assertEquals(referenceValue, stack.popValue())
    }
}
