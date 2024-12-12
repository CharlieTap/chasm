package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.fieldType
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.frameState
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewFixedExecutorTest {

    @Test
    fun `can execute the ArrayNewFixed instruction and return a correct result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val size = 2u
        val typeIndex = typeIndex(0u)
        val definedType = definedType()

        val fieldType = fieldType()
        val arrayType = arrayCompositeType(
            arrayType = arrayType(
                fieldType,
            ),
        )

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                ),
            ),
        )

        stack.push(frame)

        val executionValue1 = i32(1)
        val executionValue2 = i32(2)

        stack.pushValue(executionValue1)
        stack.pushValue(executionValue2)

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            arrayType
        }

        val fieldValue1 = executionFieldValue(executionValue1)
        val fieldValue2 = executionFieldValue(executionValue2)
        val executionValueIter = sequenceOf(executionValue2, executionValue1).iterator()
        val fieldValueIter = sequenceOf(fieldValue2, fieldValue1).iterator()
        val fieldPacker: FieldPacker = { _executionValue, _fieldType ->
            assertEquals(executionValueIter.next(), _executionValue)
            assertEquals(fieldType, _fieldType)

            Ok(fieldValueIter.next())
        }

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue1, fieldValue2),
        )
        val expected = value(ReferenceValue.Array(expectedInstance))

        val actual = ArrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, size), definedTypeExpander, fieldPacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
