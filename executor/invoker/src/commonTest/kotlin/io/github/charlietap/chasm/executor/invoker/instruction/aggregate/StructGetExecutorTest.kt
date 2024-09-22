package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instance.structAddress
import io.github.charlietap.chasm.fixture.instance.structInstance
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.executionValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.weakref.weakReference
import kotlin.test.Test
import kotlin.test.assertEquals

class StructGetExecutorTest {

    @Test
    fun `can execute the StructGetSX instruction and return a correct result`() {

        val stack = stack()

        val typeIndex = typeIndex(0u)
        val fieldIndex = fieldIndex(0u)
        val definedType = definedType()
        val signedUnpack = true

        val fieldType = fieldType()
        val fieldValue = fieldValue()
        val structType = structCompositeType(
            structType = structType(
                listOf(fieldType),
            ),
        )
        val structAddress = structAddress(0)
        val structInstance = structInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue),
        )
        val store = store(
            structs = mutableListOf(weakReference(structInstance)),
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

        val structReference = ReferenceValue.Struct(structAddress, structInstance)
        stack.pushValue(structReference)

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            structType
        }

        val fieldUnpacker: FieldUnpacker = { _fieldValue, _fieldType, _signedUnpack ->
            assertEquals(fieldValue, _fieldValue)
            assertEquals(fieldType, _fieldType)
            assertEquals(signedUnpack, _signedUnpack)

            Ok(executionValue)
        }

        val expected = value(executionValue)

        val actual = StructGetExecutor(context, typeIndex, fieldIndex, signedUnpack, definedTypeExpander, fieldUnpacker)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
