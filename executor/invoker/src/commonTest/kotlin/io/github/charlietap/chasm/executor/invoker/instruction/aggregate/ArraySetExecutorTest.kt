package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.fieldIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.fieldType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.fieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
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
            instance = moduleInstance(
                types = mutableListOf(definedType),
            ),
        )

        stack.push(frame)

        val referenceValue = arrayReferenceValue(arrayInstance)
        stack.push(referenceValue)

        val executionValue = executionValue()
        stack.push(executionValue)

        stack.push(i32(fieldIndex.index()))

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

        assertEquals(Unit, actual)
        assertEquals(expectedInstance, arrayInstance)
    }
}
