package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.finalSubType
import io.github.charlietap.chasm.fixture.ast.type.mutableFieldType
import io.github.charlietap.chasm.fixture.ast.type.recursiveType
import io.github.charlietap.chasm.fixture.ast.type.valueStorageType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.fieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayFillExecutorTest {

    @Test
    fun `can execute the ArrayFill instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val definedType = definedType(
            recursiveType = recursiveType(
                subTypes = listOf(
                    finalSubType(
                        compositeType = arrayCompositeType(
                            arrayType = arrayType(
                                fieldType = mutableFieldType(
                                    storageType = valueStorageType(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val fieldValue = fieldValue()
        val fillValue = i32()

        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )

        val context = executionContext(stack)
        val frame = frame(
            instance = moduleInstance(
                types = mutableListOf(definedType),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(arrayInstance))
        stack.pushValue(i32(2))
        stack.pushValue(fillValue)
        stack.pushValue(i32(2))

        val expectedFieldValue = executionFieldValue(fillValue)
        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(fieldValue, fieldValue, expectedFieldValue, expectedFieldValue),
        )

        val actual = ArrayFillExecutor(context, AggregateInstruction.ArrayFill(typeIndex))

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, arrayInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
