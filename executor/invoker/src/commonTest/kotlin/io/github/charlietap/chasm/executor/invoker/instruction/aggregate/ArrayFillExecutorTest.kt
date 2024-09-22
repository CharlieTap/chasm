package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.weakref.weakReference
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

        val arrayAddress = arrayAddress(0)
        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )
        val store = store(
            arrays = mutableListOf(weakReference(arrayInstance)),
        )
        val context = executionContext(stack, store)
        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(arrayAddress))
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
        assertEquals(store.arrays[0].value, expectedInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
