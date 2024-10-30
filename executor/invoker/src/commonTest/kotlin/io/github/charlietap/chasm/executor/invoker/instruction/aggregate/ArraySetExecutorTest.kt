package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.executionValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class ArraySetExecutorTest {

    @Test
    fun `can execute the ArraySet instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val fieldIndex = fieldIndex(0u)
        val definedType = definedType()

        val fieldType = fieldType()
        val fieldValue = fieldValue()
        val updatedFieldValue = executionFieldValue(i32(1))
        val structType = arrayCompositeType(
            arrayType = arrayType(
                fieldType = fieldType,
            ),
        )

        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue),
        )
        val context = executionContext(stack)

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                ),
            ),
        )

        stack.push(frame)

        val referenceValue = arrayReferenceValue(arrayInstance)
        stack.pushValue(referenceValue)

        val executionValue = executionValue()
        stack.pushValue(executionValue)

        stack.pushValue(i32(fieldIndex.index()))

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            structType
        }

        val fieldPacker: FieldPacker = { _executionValue, _fieldType ->
            assertEquals(executionValue, _executionValue)
            assertEquals(fieldType, _fieldType)

            Ok(updatedFieldValue)
        }

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(updatedFieldValue),
        )

        val actual = ArraySetExecutor(context, AggregateInstruction.ArraySet(typeIndex), definedTypeExpander, fieldPacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, arrayInstance)
    }
}
