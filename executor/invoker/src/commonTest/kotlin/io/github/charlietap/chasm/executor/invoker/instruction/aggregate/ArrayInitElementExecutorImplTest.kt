package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.gc.weakReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.elementAddress
import io.github.charlietap.chasm.fixture.instance.elementInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.elementIndex
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
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayInitElementExecutorImplTest {

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

        val arrayAddress = arrayAddress(0)
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
            arrays = mutableListOf(
                weakReference(arrayInstance),
            ),
            elements = mutableListOf(
                elementInstance,
            ),
        )
        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(
                        definedType,
                    ),
                    elemAddresses = mutableListOf(
                        elementAddress,
                    ),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(arrayAddress))
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

        val actual = ArrayInitElementExecutorImpl(store, stack, typeIndex, elementIndex)

        assertEquals(Ok(Unit), actual)
        assertEquals(store.arrays[0].value, expectedInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
