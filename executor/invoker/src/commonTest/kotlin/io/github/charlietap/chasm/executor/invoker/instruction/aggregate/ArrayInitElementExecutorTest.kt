package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.finalSubType
import io.github.charlietap.chasm.fixture.ast.type.mutableFieldType
import io.github.charlietap.chasm.fixture.ast.type.recursiveType
import io.github.charlietap.chasm.fixture.ast.type.valueStorageType
import io.github.charlietap.chasm.fixture.ast.type.varMutability
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayInitElementExecutorTest {

    @Test
    fun `can execute the ArrayInitElement instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val elementIndex = elementIndex(0u)
        val fieldValue = executionFieldValue(i32(0))
        val definedType = definedType(
            recursiveType = recursiveType(
                subTypes = listOf(
                    finalSubType(
                        compositeType = arrayCompositeType(
                            arrayType = arrayType(
                                fieldType = mutableFieldType(
                                    storageType = valueStorageType(),
                                    mutability = varMutability(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )
        val elementToCopy = i31ReferenceValue()
        val elementAddress = elementAddress(0)
        val elementInstance = elementInstance(
            elements = arrayOf(
                elementToCopy,
            ),
        )
        val store = store(
            elements = mutableListOf(
                elementInstance,
            ),
        )
        val context = executionContext(stack, store)
        val frame = frame(
            instance = moduleInstance(
                types = mutableListOf(
                    definedType,
                ),
                elemAddresses = mutableListOf(
                    elementAddress,
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(arrayInstance))
        stack.pushValue(i32(0))
        stack.pushValue(i32(0))
        stack.pushValue(i32(1))

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(
                executionFieldValue(elementToCopy),
                fieldValue,
                fieldValue,
                fieldValue,
            ),
        )

        val actual = ArrayInitElementExecutor(context, AggregateInstruction.ArrayInitElement(typeIndex, elementIndex))

        assertEquals(Unit, actual)
        assertEquals(expectedInstance, arrayInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
