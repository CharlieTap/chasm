package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
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
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.fieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.structReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class StructSetExecutorTest {

    @Test
    fun `can execute the StructSet instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val fieldIndex = fieldIndex(0u)
        val definedType = definedType()

        val fieldType = fieldType()
        val fieldValue = fieldValue()
        val updatedFieldValue = executionFieldValue(i32(1))
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

        val frame = frame(
            instance = moduleInstance(
                types = mutableListOf(definedType),
            ),
        )

        stack.push(frame)

        val referenceValue = structReferenceValue(structInstance)
        stack.pushValue(referenceValue)

        val executionValue = executionValue()
        stack.pushValue(executionValue)

        val definedTypeExpander: DefinedTypeExpander = {
            assertEquals(definedType, it)

            structType
        }

        val fieldPacker: FieldPacker = { _executionValue, _fieldType ->
            assertEquals(executionValue, _executionValue)
            assertEquals(fieldType, _fieldType)

            Ok(updatedFieldValue)
        }

        val instruction = AggregateInstruction.StructSet(typeIndex, fieldIndex)

        val expectedInstance = structInstance(
            definedType = definedType,
            fields = mutableListOf(updatedFieldValue),
        )

        val actual = StructSetExecutor(context, instruction, definedTypeExpander, fieldPacker)

        assertEquals(Unit, actual)
        assertEquals(expectedInstance, structInstance)
    }
}
