package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.executionValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.weakref.weakReference
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayGetExecutorTest {

    @Test
    fun `can execute the ArrayGetSX instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val fieldIndex = fieldIndex(0u)
        val definedType = definedType()
        val signedUnpack = true

        val fieldType = fieldType()
        val fieldValue = fieldValue()
        val arrayType = arrayCompositeType(
            arrayType = arrayType(
                fieldType = fieldType,
            ),
        )
        val arrayAddress = arrayAddress(0)
        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue),
        )
        val store = store(
            arrays = mutableListOf(weakReference(arrayInstance)),
        )
        val context = executionContext(stack, store)
        val executionValue = executionValue()

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                ),
            ),
        )

        stack.push(frame)

        val arrayReference = ReferenceValue.Array(arrayAddress, arrayInstance)
        stack.pushValue(arrayReference)

        stack.pushValue(i32(fieldIndex.index()))

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            arrayType
        }

        val fieldUnpacker: FieldUnpacker = { _fieldValue, _fieldType, _signedUnpack ->
            assertEquals(fieldValue, _fieldValue)
            assertEquals(fieldType, _fieldType)
            assertEquals(signedUnpack, _signedUnpack)

            Ok(executionValue)
        }

        val expected = value(executionValue)

        val actual = ArrayGetExecutor(context, typeIndex, signedUnpack, definedTypeExpander, fieldUnpacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
