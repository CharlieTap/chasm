package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instance.structInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class StructNewExecutorTest {

    @Test
    fun `can execute the StructNew instruction and return a correct result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val typeIndex = typeIndex(0u)
        val definedType = definedType()

        val fieldType = fieldType()
        val structType = structCompositeType(
            structType = structType(
                listOf(fieldType, fieldType),
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

        val executionValue1 = i32(0)
        val executionValue2 = i32(1)

        stack.pushValue(executionValue1)
        stack.pushValue(executionValue2)

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            structType
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

        val expectedInstance = structInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue1, fieldValue2),
        )
        val expected = value(ReferenceValue.Struct(expectedInstance))

        val actual = StructNewExecutor(context, AggregateInstruction.StructNew(typeIndex), definedTypeExpander, fieldPacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
