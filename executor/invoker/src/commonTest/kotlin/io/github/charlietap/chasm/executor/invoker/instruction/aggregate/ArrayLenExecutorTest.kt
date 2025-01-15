package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.fieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayLenExecutorTest {

    @Test
    fun `can execute the ArrayLen instruction and return a correct result`() {

        val stack = stack()
        val fieldValue = fieldValue()

        val arrayInstance = arrayInstance(
            definedType = definedType(),
            fields = mutableListOf(fieldValue, fieldValue, fieldValue),
        )
        val context = executionContext(stack)

        val referenceValue = arrayReferenceValue(arrayInstance)
        stack.pushValue(referenceValue)

        val actual = ArrayLenExecutor(context, AggregateInstruction.ArrayLen)

        assertEquals(Unit, actual)
        assertEquals(i32(3), stack.popValueOrNull())
    }
}
