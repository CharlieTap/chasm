package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewFixedExecutorTest {

    @Test
    fun `can execute the ArrayNewFixed instruction and return a correct result`() {

        val store = store()
        val stack = stack()
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
        val expected = value(ReferenceValue.Array(Address.Array(0), expectedInstance))

        val actual = ArrayNewFixedExecutor(store, stack, typeIndex, size, definedTypeExpander, fieldPacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(store.arrays[0].value, expectedInstance)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
