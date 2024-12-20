package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.fieldIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.fieldType
import io.github.charlietap.chasm.fixture.ast.type.structCompositeType
import io.github.charlietap.chasm.fixture.ast.type.structType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.fieldValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
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
        val structInstance = structInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue),
        )
        val context = executionContext(stack)
        val executionValue = executionValue()

        val frame = frame(
            instance = moduleInstance(
                types = mutableListOf(definedType),
            ),
        )

        stack.push(frame)

        val structReference = ReferenceValue.Struct(structInstance)
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
